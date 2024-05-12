package com.osio.userservice.domain.user.service;

import com.osio.userservice.domain.client.order.OrderClient;
import com.osio.userservice.domain.user.dto.UserRequestDto;
import com.osio.userservice.domain.user.dto.UserResponseDto;
import com.osio.userservice.domain.user.entity.User;
import com.osio.userservice.domain.user.entity.UserRoleEnum;
import com.osio.userservice.domain.user.repository.UserRepository;
import com.osio.userservice.global.jwt.JwtToken;
import com.osio.userservice.global.jwt.JwtTokenProvider;
import com.osio.userservice.global.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrderClient wishlistClient;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EncryptUtil encryptUtil;
    private final RedisService redisService;

    // 이메일 중복확인
    @Transactional
    public boolean emailCheck(String email) {
        String encryptedEmail = encryptUtil.encrypt(email);

        /*
        존재 여부를 표현하는 래퍼(wrapper) 클래스
        null 을 반환하는 메서드의 반환 값을 대체하거나, NullPointerException 을 방지
         */
        Optional<User> findUser = userRepository.findByEmail(encryptedEmail);
        return findUser.isPresent();
    }

    // 회원가입
    @Transactional
    public String signup(UserRequestDto.SignupDto signupDto) {
        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(signupDto.getPassword());

        // 이메일, 이름, 전화번호, 주소 암호화
        String encryptedEmail = encryptUtil.encrypt(signupDto.getEmail());
        String encryptedName = encryptUtil.encrypt(signupDto.getName());
        String encryptedPhone = encryptUtil.encrypt(signupDto.getPhone());
        String encryptedAddress = encryptUtil.encrypt(signupDto.getAddress());

        User user = User.builder()
                .email(encryptedEmail)
                .password(hashedPassword)
                .name(encryptedName)
                .phone(encryptedPhone)
                .address(encryptedAddress)
                .role(UserRoleEnum.USER)
                .build();

        try {
            userRepository.save(user);

            wishlistClient.createWishlist(user.getUserId());
            return "Success";
        } catch (DataIntegrityViolationException e) {   // 중복된 이메일 주소로 회원가입 시도한 경우 예외 처리
            return "Duplicate";
        }
    }

    // 로그인
    @Transactional
    public JwtToken signin(UserRequestDto.SigninDto signinDto) {
        String email = signinDto.getEmail();
        String password = signinDto.getPassword();
        String encryptedEmail = encryptUtil.encrypt(email);

        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 제공된 비밀번호와 데이터베이스에 저장된 해시된 비밀번호를 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 User 에 대한 검증 진행
        // authenticationManagerBuilder 가 요청을 수행할 provider 를 찾음
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        // UserDetailsImpl 을 담은 authentication
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        log.info("request email = {}, password = {}", email, password);

        // ----- 로그인 로직 무조건 필수 -----

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // ----- 프론트단 없이 로그아웃 구현 위해 추가 -----
        
        // redis 에 토큰 저장
        // redisService.setTokenValues(jwtToken.getAccessToken(), email + " jwtToken");

        return jwtToken;
    }

    // 로그아웃
    public void logout(String token, String userId) {
        log.info("UserService.logout()");

        // redis 에 토큰 삭제
        // redisService.deleteValues(token);
        
        // redis 에 토큰 저장 - 블랙리스트
        redisService.setTokenValues(token, userId + " jwtToken");
    }

    // 마이페이지 - 내 정보 조회
    @Transactional
    public UserResponseDto.MyPageDto myPage(Long userId) {
        log.info("UserService.myPage()");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String email = encryptUtil.decrypt(user.getEmail());
        String name = encryptUtil.decrypt(user.getName());
        String phone = encryptUtil.decrypt(user.getPhone());
        String address = encryptUtil.decrypt(user.getAddress());

        return UserResponseDto.MyPageDto.builder()
                .email(email)
                .name(name)
                .phone(phone)
                .address(address)
                .build();
    }

    // 비밀번호 업데이트
    @Transactional
    public void updatePassword(UserRequestDto.ChangePasswordDto changePasswordDto, Long userId) {
        log.info("UserService.updatePassword()");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 제공된 비밀번호와 데이터베이스에 저장된 해시된 비밀번호를 비교
        if (!passwordEncoder.matches(changePasswordDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String encryptedNewPwd = passwordEncoder.encode(changePasswordDto.getNewPassword());
        user.updatePassword(encryptedNewPwd);
        userRepository.save(user);
    }

    // 폰 번호 업데이트
    @Transactional
    public void updatePhone(UserRequestDto.ChangePhoneDto changePhoneDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String encryptedPhone = encryptUtil.encrypt(changePhoneDto.getPhone());
        user.updatePhone(encryptedPhone);
        userRepository.save(user);
    }

    // 주소 업데이트
    @Transactional
    public void updateAddress(UserRequestDto.ChangeAddressDto changeAddressDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String encryptedAddress = encryptUtil.encrypt(changeAddressDto.getAddress());
        user.updateAddress(encryptedAddress);
        userRepository.save(user);
    }

}
