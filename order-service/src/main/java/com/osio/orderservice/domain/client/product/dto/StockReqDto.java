package com.osio.orderservice.domain.client.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class StockReqDto {

    @Getter
    @AllArgsConstructor
    public static class StockQuantityDto {
        private long productId;

        private int quantity;

        private long orderId;
    }
}
