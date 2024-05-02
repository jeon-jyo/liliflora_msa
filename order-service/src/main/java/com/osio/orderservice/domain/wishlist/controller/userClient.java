package com.osio.orderservice.domain.wishlist.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface userClient {

    @GetMapping("/api/internal/user/{userId}")
    Long getUserId(@PathVariable("userId") Long userId);
}
