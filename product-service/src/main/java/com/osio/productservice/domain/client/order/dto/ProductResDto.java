package com.osio.productservice.domain.client.order.dto;

import com.osio.productservice.domain.product.entity.Product;
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

        public static ProductResDto.ProductDetailDto fromEntity(Product product) {
            return ProductResDto.ProductDetailDto.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .build();
        }
    }

}
