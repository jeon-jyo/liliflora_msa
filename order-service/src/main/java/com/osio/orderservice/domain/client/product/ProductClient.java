package com.osio.orderservice.domain.client.product;

import com.osio.orderservice.domain.client.product.dto.ProductReqDto;
import com.osio.orderservice.domain.client.product.dto.ProductResDto;
import com.osio.orderservice.domain.client.product.dto.StockReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductClient {

    // 상품 정보
    @GetMapping("/api/internal/product/detail/{productId}")
    ProductResDto.ProductDetailDto getProductDetail(@PathVariable("productId") long productId);

    // 상품 재고 감소
    @PostMapping("/api/internal/product/decrease")
    void decreaseQuantity(@RequestBody ProductReqDto.ProductQuantityDto productQuantityDto);

    // 상품 재고 복구
    @PostMapping("/api/internal/product/increase")
    void increaseQuantity(@RequestBody ProductReqDto.ProductQuantityDto productQuantityDto);

    // redis 재고 감소
    @PostMapping("/api/internal/stock/decrease")
    boolean decreaseStockQuantity(@RequestBody StockReqDto.StockQuantityDto stockQuantityDto) throws Exception;

    // redis 재고 복구
    @PostMapping("/api/internal/stock/increase")
    void increaseStockQuantity(@RequestBody StockReqDto.StockQuantityDto stockQuantityDto);
}
