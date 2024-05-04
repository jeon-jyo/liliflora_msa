package com.osio.userservice.domain.user.controller;

import com.osio.userservice.domain.user.dto.UserResponseDto;
import com.osio.userservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/internal/user")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;

    // 주문 유저 정보
    @GetMapping("/myPage/{userId}")
    UserResponseDto.MyPageDto myPage(@PathVariable("userId") long userId) {
        log.info("UserInternalController.myPage()");

        return userService.myPage(userId);
    }
}
