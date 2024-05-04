package com.osio.apigatewayserver.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

// Spring Security 와 JWT 토큰을 사용하여 인증과 권한 부여를 처리하는 클래스
// JWT 토큰의 생성, 복호화, 검증 기능을 구현
@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    // application.yml (=.properties) 에서 secret 값 가져와서 key 에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);    // 지정된 키 바이트 배열을 기반으로 새 SecretKey 인스턴스를 만듦
    }

    // 토큰 정보를 검증하는 메서드 (유효성을 확인)
    public boolean validateToken(String token) {
        try {
            // 토큰의 서명 키를 설정하고, 예외 처리를 통해 토큰의 유효성 여부를 판단
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {  // 토큰이 올바른 형식이 아니거나 클레임이 비어있는 경우 등에 발생
            log.info("JWT claims string is empty.", e);
        }
        return false;

        /*
        claim.getSubject()는 주어진 토큰의 클레임에서 "sub" 클레임의 값을 반환
        -> 토큰의 주체를 나타냄. ex) 사용자의 식별자나 이메일 주소
         */
    }

    // accessToken
    // parseClaims -> getSubject
    public String getSubject(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims() + " ";
        }

        /*
        클레임(Claims): 토큰에서 사용할 정보의 조각

        주어진 Access Token 을 복호화하고, 만료된 토큰인 경우에도 Claims 반환
        parseClaimsJws() 메서드가 JWT 토큰의 검증과 파싱을 모두 수행
         */
    }

}
