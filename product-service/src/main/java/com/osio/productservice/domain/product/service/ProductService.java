package com.osio.productservice.domain.product.service;

import com.osio.productservice.domain.client.order.dto.ProductResDto;
import com.osio.productservice.domain.client.order.dto.ProductReqDto;
import com.osio.productservice.domain.product.entity.Product;
import com.osio.productservice.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @RequiredArgsConstructor - 롬복이 자동으로 생성자를 생성
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 목록
    @Transactional
    public List<com.osio.productservice.domain.product.dto.ProductResponseDto.ProductDetailDto> productList() {
        return productRepository.findAllByOrderByProductIdDesc().stream()
                .map(com.osio.productservice.domain.product.dto.ProductResponseDto.ProductDetailDto::fromEntity)
                .toList();
    }

    // 상품 상세
    @Transactional
    public com.osio.productservice.domain.product.dto.ProductResponseDto.ProductDetailDto productDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return com.osio.productservice.domain.product.dto.ProductResponseDto.ProductDetailDto.fromEntity(product);
    }

    // feign

    // 상품 정보
    @Transactional
    public ProductResDto.ProductDetailDto getProductDetail(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return ProductResDto.ProductDetailDto.fromEntity(product);
    }

    // 상품 재고 감소
    @Transactional
    public void decreaseQuantity(ProductReqDto.ProductQuantityDto productQuantityDto) {
        Product product = productRepository.findById(productQuantityDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        log.info("productQuantityDto " + productQuantityDto.getProductId());
        log.info("getProductId " + product.getProductId());

        product.decreaseQuantity(productQuantityDto.getQuantity());
    }

    // 상품 재고 복구
    @Transactional
    public void increaseQuantity(ProductReqDto.ProductQuantityDto productQuantityDto) {
        Product product = productRepository.findById(productQuantityDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.increaseQuantity(productQuantityDto.getQuantity());
    }
}
