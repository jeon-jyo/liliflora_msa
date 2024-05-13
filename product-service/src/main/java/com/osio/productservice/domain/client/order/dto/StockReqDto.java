package com.osio.productservice.domain.client.order.dto;

import lombok.Builder;
import lombok.Getter;

public class StockReqDto {

    @Getter
    @Builder
    public static class StockQuantityDto {
        private long productId;

        private int quantity;

        private long orderId;
    }

}
