package com.osio.userservice.domain.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class UserRequestDto {

    // 회원가입시에 필요한 attribute 만
    @Getter
    public static class SignupDto {

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;

        @NotBlank(message = "핸드폰번호는 필수 입력 값입니다.")
        private String phone;

        @NotBlank(message = "주소는 필수 입력 값입니다.")
        private String address;
    }

    @Getter
//    @Setter
    public static class EmailCheckDto {
        /*
        1) @기호를 포함해야 한다.
        2 _@기호를 기준으로 이메일 주소를 이루는 로컬호스트와 도메인 파트가 존재해야 한다.
        3) 도메인 파트는 최소하나의 점과 그 뒤에 최소한 2개의 알파벳을 가진다를 검증
         */
        @Email
        @NotEmpty(message = "이메일을 입력해 주세요")
        private String email;

        private String authNumber;  // 인증번호
    }

    @Getter
//    @Setter
//    @ToString
//    @NoArgsConstructor
    public static class SigninDto {
        private String email;
        private String password;
    }

    @Getter
//    @Setter
//    @NoArgsConstructor
    public static class ChangePasswordDto {
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자입니다.")
        private String password;

        @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String newPassword;
    }

    @Getter
//    @Setter
//    @NoArgsConstructor
    public static class ChangePhoneDto {
        private String phone;
    }

    @Getter
//    @Setter
//    @NoArgsConstructor
    public static class ChangeAddressDto {
        private String address;
    }
}
