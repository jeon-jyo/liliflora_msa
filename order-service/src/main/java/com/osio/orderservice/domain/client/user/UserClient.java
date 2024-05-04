package com.osio.orderservice.domain.client.user;

import com.osio.orderservice.domain.client.user.dto.UserResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    // 주문 유저 정보
    @GetMapping("/api/internal/user/myPage/{userId}")
    UserResDto.MyPageDto myPage(@PathVariable("userId") long userId);
}
