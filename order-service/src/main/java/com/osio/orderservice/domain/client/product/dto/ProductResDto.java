package com.osio.orderservice.domain.client.product.dto;

import lombok.Builder;
import lombok.Getter;

public class ProductResDto {

    @Getter
    @Builder
    public static class ProductDetailDto {
        private long productId;

        private String name;

        private int price;

        private int quantity;
    }

}
