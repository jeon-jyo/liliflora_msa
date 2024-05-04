package com.osio.apigatewayserver.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

// Redis 에 저장, 조회, 삭제하는 메서드를 구현하는 클래스인 RedisTemplate 를 주입받아 Redis 데이터를 조작
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 해당 키가 있는지 확인
    public boolean hasKeyList(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    // 해당 키가 있는지 확인 - 블랙리스트
    public boolean hasKeyBlacklist(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}