package com.osio.orderservice.domain.order.dto;

import lombok.Getter;

public class OrderRequestDto {

    @Getter
    public static class OrderDetailDto {
        private long orderId;
    }
}
