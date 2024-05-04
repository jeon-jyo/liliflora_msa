package com.osio.orderservice.domain.client.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserResDto {

    @Getter
    @AllArgsConstructor
    public static class MyPageDto {
        private String email;
        private String name;
        private String phone;
        private String address;
    }
}
