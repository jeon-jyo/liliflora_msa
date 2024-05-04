package com.osio.productservice.domain.product.controller;

import com.osio.productservice.domain.client.order.dto.ProductResDto;
import com.osio.productservice.domain.client.order.dto.ProductReqDto;
import com.osio.productservice.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/internal/product")
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductService productService;

    // 상품 정보
    @GetMapping("/detail/{productId}")
    ProductResDto.ProductDetailDto getProductDetail(@PathVariable("productId") long productId) {
        log.info("ProductInternalController.getProductDetail()");

        return productService.getProductDetail(productId);
    }

    // 상품 재고 감소
    @GetMapping("/decrease")
    void decreaseQuantity(@RequestBody ProductReqDto.ProductQuantityDto productQuantityDto) {
        log.info("ProductInternalController.decreaseQuantity()");

        productService.decreaseQuantity(productQuantityDto);
    }

    // 상품 재고 복구
    @GetMapping("/increase")
    void increaseQuantity(@RequestBody ProductReqDto.ProductQuantityDto productQuantityDto) {
        log.info("ProductInternalController.increaseQuantity()");

        productService.increaseQuantity(productQuantityDto);
    }
}
