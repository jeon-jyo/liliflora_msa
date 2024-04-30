package com.osio.productservice.domain.product.controller;

import com.osio.productservice.domain.product.dto.ProductResponseDto;
import com.osio.productservice.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 목록
    @GetMapping("/list")
    public List<ProductResponseDto.ProductDetailDto> productList() {
        log.info("ProductController.productList()");

        return productService.productList();
    }

    // 상품 상세
    @GetMapping("/{productId}")
    public ProductResponseDto.ProductDetailDto productDetail(@PathVariable("productId") Long productId) {
        log.info("ProductController.productDetail()");

        return productService.productDetail(productId);
    }
}
