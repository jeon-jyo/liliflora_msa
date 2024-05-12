package com.osio.productservice.domain.stock.service;

import com.osio.productservice.domain.client.order.dto.ProductReqDto;
import com.osio.productservice.domain.product.entity.Product;
import com.osio.productservice.domain.product.repository.ProductRepository;
import com.osio.productservice.domain.stock.entity.Stock;
import com.osio.productservice.domain.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    // 1분마다 재고 동기화
    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void syncProductQuantity() {
        Iterable<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            if (stock != null) {
                long productId = stock.getStockId();
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found " + productId));

                product.syncQuantity(stock.getQuantity());
            }
        }
    }

    // feign

    // redis 재고 감소
    @Transactional
    public boolean decreaseStockQuantity(ProductReqDto.ProductQuantityDto productQuantityDto) throws Exception {
        log.info("StockService.decreaseStockQuantity()");

        long orderProductId = productQuantityDto.getProductId();
        int orderQuantity = productQuantityDto.getQuantity();

        Product product = productRepository.findById(orderProductId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // redis 재고 확인
        Stock stock = stockRepository.findById(orderProductId)
                .orElseGet(() -> {
                    // 없으면 새로운 Stock 객체 생성
                    Stock newStock = new Stock(orderProductId, product.getQuantity());
                    return stockRepository.save(newStock); // 새로운 재고 redis 저장하고 반환
                });

        if (stock.getQuantity() < orderQuantity) {
            return false;
        }

        stock.decreaseQuantity(orderQuantity);  // redis 재고 감소
        stockRepository.save(stock);
        return true;
    }

    // redis 재고 복구
    @Transactional
    public void increaseStockQuantity(ProductReqDto.ProductQuantityDto productQuantityDto) {
        log.info("StockService.increaseStockQuantity()");

        long orderProductId = productQuantityDto.getProductId();
        int orderQuantity = productQuantityDto.getQuantity();

        Product product = productRepository.findById(orderProductId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // redis 재고 확인
        Stock stock = stockRepository.findById(orderProductId)
                .orElseGet(() -> {
                    // 없으면 새로운 Stock 객체 생성
                    Stock newStock = new Stock(orderProductId, product.getQuantity());
                    return stockRepository.save(newStock); // 새로운 재고 redis 저장하고 반환
                });
        System.out.println("복구 전 : " + stock.getQuantity());

        stock.increaseQuantity(orderQuantity);  // redis 재고 복구
        stockRepository.save(stock);
        System.out.println("복구 후 : " + stock.getQuantity());
    }
}
