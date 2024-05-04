package com.osio.productservice.domain.client.order.dto;

import com.osio.productservice.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

public class ProductReqDto {

    @Getter
    @Builder
    public static class ProductQuantityDto {
        private long productId;

        private int quantity;

        public static ProductReqDto.ProductQuantityDto fromEntity(Product product) {
            return ProductReqDto.ProductQuantityDto.builder()
                    .productId(product.getProductId())
                    .quantity(product.getQuantity())
                    .build();
        }
    }

}
