package com.osio.productservice.domain.client.order.dto;

import lombok.Builder;
import lombok.Getter;

public class ProductReqDto {

    @Getter
    @Builder
    public static class ProductQuantityDto {
        private long productId;

        private int quantity;
    }

}
