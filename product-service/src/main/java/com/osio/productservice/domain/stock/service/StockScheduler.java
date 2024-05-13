package com.osio.productservice.domain.stock.service;

import com.osio.productservice.domain.product.entity.Product;
import com.osio.productservice.domain.product.repository.ProductRepository;
import com.osio.productservice.domain.stock.entity.Stock;
import com.osio.productservice.domain.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockScheduler {

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

                if (product.getProductId() != stock.getQuantity()) {
                    product.syncQuantity(stock.getQuantity());
                }
            }
        }
    }
}
