package com.osio.productservice.domain.product.service;

import com.osio.productservice.domain.product.dto.ProductResponseDto;
import com.osio.productservice.domain.product.entity.Product;
import com.osio.productservice.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.model.NotFoundException;
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
    public List<ProductResponseDto.ProductDetailDto> productList() {
        return productRepository.findAllByOrderByProductIdDesc().stream()
                .map(ProductResponseDto.ProductDetailDto::fromEntity)
                .toList();
    }

    // 상품 상세
    @Transactional
    public ProductResponseDto.ProductDetailDto productDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        return ProductResponseDto.ProductDetailDto.fromEntity(product);
    }
}
