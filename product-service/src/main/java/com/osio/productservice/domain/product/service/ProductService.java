package com.osio.productservice.domain.product.service;

import com.osio.productservice.domain.client.order.dto.ProductResDto;
import com.osio.productservice.domain.client.order.dto.ProductReqDto;
import com.osio.productservice.domain.product.entity.Product;
import com.osio.productservice.domain.product.repository.ProductRepository;
import com.osio.productservice.domain.stock.entity.Stock;
import com.osio.productservice.domain.stock.repository.StockRepository;
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
    private final StockRepository stockRepository;

    // 상품 목록
    @Transactional
    public List<com.osio.productservice.domain.product.dto.ProductResponseDto.ProductDetailDto> productList() {
        log.info("ProductService.productList()");

        return productRepository.findAllByOrderByProductIdDesc().stream()
                .map(com.osio.productservice.domain.product.dto.ProductResponseDto.ProductDetailDto::fromEntity)
                .toList();
    }

    // 상품 상세
    @Transactional
    public com.osio.productservice.domain.product.dto.ProductResponseDto.ProductDetailDto productDetail(Long productId) {
        log.info("ProductService.productDetail()");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Stock stock = stockRepository.findById(productId)
                .orElseGet(() -> {
                    // 없으면 새로운 Stock 객체 생성
                    Stock newStock = new Stock(product.getProductId(), product.getQuantity());
                    return stockRepository.save(newStock); // 새로운 재고 redis 저장하고 반환
                });

        return com.osio.productservice.domain.product.dto.ProductResponseDto.ProductDetailDto.fromEntityAndQuantity(product, stock.getQuantity());
    }

    // feign

    // 상품 정보
    @Transactional
    public ProductResDto.ProductDetailDto getProductDetail(long productId) {
        log.info("ProductService.getProductDetail()");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        return ProductResDto.ProductDetailDto.fromEntity(product);
    }

    // 상품 재고 감소
    @Transactional
    public void decreaseQuantity(ProductReqDto.ProductQuantityDto productQuantityDto) {
        log.info("ProductService.decreaseQuantity()");

        Product product = productRepository.findById(productQuantityDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        log.info("productQuantityDto " + productQuantityDto.getProductId());
        log.info("getProductId " + product.getProductId());

        product.decreaseQuantity(productQuantityDto.getQuantity());
    }

    // 상품 재고 복구
    @Transactional
    public void increaseQuantity(ProductReqDto.ProductQuantityDto productQuantityDto) {
        log.info("ProductService.increaseQuantity()");

        Product product = productRepository.findById(productQuantityDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.increaseQuantity(productQuantityDto.getQuantity());
    }
}
