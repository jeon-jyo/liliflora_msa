package com.osio.userservice.domain.user.controller;

import com.osio.userservice.domain.user.dto.ResponseDto;
import com.osio.userservice.domain.user.dto.UserRequestDto;
import com.osio.userservice.domain.user.dto.UserResponseDto;
import com.osio.userservice.domain.user.service.MailSendService;
import com.osio.userservice.domain.user.service.UserService;
import com.osio.userservice.global.jwt.JwtToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailSendService mailService;

    // 이메일 중복확인 및 인증번호 발송
    @PostMapping("/mailSend")
    public String mailSend(@RequestBody @Valid UserRequestDto.EmailCheckDto emailRequestDto) {
        log.info("UserController.mailSend()");

        String email = emailRequestDto.getEmail();
        String result = "이미 존재하는 이메일 입니다.";
        if (!userService.emailCheck(email)) {
            result = mailService.joinEmail(email);
        }
        return result;
    }

    // 인증번호 검사
    @PostMapping ("/mailAuthCheck")
    public String mailAuthCheck(@RequestBody @Valid UserRequestDto.EmailCheckDto emailRequestDto) {
        log.info("UserController.mailAuthCheck()");

        return mailService.checkAuthNumber(emailRequestDto);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseDto<Object> signup(@RequestBody @Valid UserRequestDto.SignupDto signupDto) {   // @RequestBody 는 json 객체로 넘어오는 것을 받아준다
        log.info("UserController.signup()");

        String result = userService.signup(signupDto);
        if (result.equals("Success")) {
            return ResponseDto.of(HttpStatus.CREATED, "가입 성공");
        } else if (result.equals("Duplicate")) {
            return ResponseDto.of(HttpStatus.CONFLICT, "중복된 이메일 입니다.");
        } else {
            return ResponseDto.of(HttpStatus.BAD_REQUEST, "가입 실패");
        }
    }

    // 로그인
    @PostMapping("/signin")
    public JwtToken signin(@RequestBody UserRequestDto.SigninDto signinDto) {
        log.info("UserController.signin()");

        JwtToken jwtToken = userService.signin(signinDto);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;    // Access Token 발급
    }

    /*
    Spring Security 의 인증된 사용자(principal - UserDetails 객체) 정보는 Authentication 객체를 통해 접근됨
    Authentication 객체에서 principal 을 추출하여 해당 필드에 직접 액세스할 수 있음

    UserDetails 로 principal 을 만들었기 때문에 UserDetailsImpl 는 null 이고 UserDetails 로 받기

    헤더로 토큰을 넘겨주면 필터가 먼저 토큰을 확인함 (바디에서는 사용자 정보 넘겨 줄 필요 x)
     */
    // 마이페이지 - 내 정보 조회
    // @AuthenticationPrincipal UserDetails userDetails
    @GetMapping("/myPage")
    public UserResponseDto.MyPageDto myPage(@RequestHeader(value = "userId") String userId) {
        log.info("UserController.myPage()");

        return userService.myPage(Long.valueOf(userId));
    }

    // 비밀번호 업데이트
    @PutMapping("/password")
    public boolean updatePassword(@RequestBody @Valid UserRequestDto.ChangePasswordDto changePasswordDto,
                                  @RequestHeader(value = "userId") String userId) {
        log.info("UserController.updatePassword()");

        userService.updatePassword(changePasswordDto, Long.valueOf(userId));
        return true;
    }

    // 폰 번호 업데이트
    @PutMapping("/phone")
    public boolean updatePhone(@RequestBody UserRequestDto.ChangePhoneDto changePhoneDto, @RequestHeader(value = "userId") String userId) {
        log.info("UserController.updatePhone()");

        userService.updatePhone(changePhoneDto, Long.valueOf(userId));
        return true;
    }

    // 주소 업데이트
    @PutMapping("/address")
    public boolean updateAddress(@RequestBody UserRequestDto.ChangeAddressDto changeAddressDto, @RequestHeader(value = "userId") String userId) {
        log.info("UserController.updateAddress()");

        userService.updateAddress(changeAddressDto, Long.valueOf(userId));
        return true;
    }

}
