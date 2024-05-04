package com.osio.orderservice.domain.client.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProductReqDto {

    @Getter
    @AllArgsConstructor
    public static class ProductQuantityDto {
        private long productId;

        private int quantity;
    }
}
