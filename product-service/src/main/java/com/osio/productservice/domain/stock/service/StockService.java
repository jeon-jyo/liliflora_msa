package com.osio.productservice.domain.stock.service;

import com.osio.productservice.domain.client.order.dto.StockReqDto;
import com.osio.productservice.domain.product.entity.Product;
import com.osio.productservice.domain.product.repository.ProductRepository;
import com.osio.productservice.domain.stock.entity.Stock;
import com.osio.productservice.domain.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final RedissonClient redissonClient;

    // feign

    // redis 재고 감소
    @Transactional
    public boolean decreaseStockQuantity(StockReqDto.StockQuantityDto stockQuantityDto) throws Exception {
        log.info("StockService.decreaseStockQuantity()");

        long orderProductId = stockQuantityDto.getProductId();
        int orderQuantity = stockQuantityDto.getQuantity();

        Product product = productRepository.findById(orderProductId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        String lockName = "ORDER" + stockQuantityDto.getProductId();
        RLock rLock = redissonClient.getLock(lockName);
        long waitTime = 5L;
        long leaseTime = 3L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        try {
            boolean available = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if(!available){
                throw new IllegalArgumentException("Lock acquired");
            }
            //=== 락 획득 후 로직 수행 ===
            // redis 재고 확인
            Stock stock = stockRepository.findById(orderProductId)
                    .orElseGet(() -> {
                        // 없으면 새로운 Stock 객체 생성
                        Stock newStock = new Stock(orderProductId, product.getQuantity());
                        return stockRepository.save(newStock); // 새로운 재고 redis 저장하고 반환
                    });
            System.out.println("감소 전 : " + stock.getQuantity());
            
            if (stock.getQuantity() < orderQuantity) {
                return false;
            }

            stock.decreaseQuantity(orderQuantity);  // redis 재고 감소
            stockRepository.save(stock);
            System.out.println("감소 후 : " + stock.getQuantity());
            return true;
            // === 로직 수행 완료 ===
        }catch (InterruptedException e){
            //락을 얻으려고 시도하다가 인터럽트를 받았을 때 발생하는 예외
            throw new IllegalArgumentException("Lock acquired");
        }finally{
            try{
                rLock.unlock();
                log.info("unlock complete: {}", rLock.getName());
            }catch (IllegalMonitorStateException e){
                //이미 종료된 락일 때 발생하는 예외
                throw new IllegalArgumentException("Lock acquired");
            }
        }
    }

    // redis 재고 복구
    @Transactional
    public void increaseStockQuantity(StockReqDto.StockQuantityDto stockQuantityDto) {
        log.info("StockService.increaseStockQuantity()");

        long orderProductId = stockQuantityDto.getProductId();
        int orderQuantity = stockQuantityDto.getQuantity();

        Product product = productRepository.findById(orderProductId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        String lockName = "ORDER" + stockQuantityDto.getProductId();
        RLock rLock = redissonClient.getLock(lockName);
        long waitTime = 5L;
        long leaseTime = 3L;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        try {
            boolean available = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if(!available){
                throw new IllegalArgumentException("Lock acquired");
            }
            //=== 락 획득 후 로직 수행 ===
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
            // === 로직 수행 완료 ===
        }catch (InterruptedException e){
            //락을 얻으려고 시도하다가 인터럽트를 받았을 때 발생하는 예외
            throw new IllegalArgumentException("Lock acquired");
        }finally{
            try{
                rLock.unlock();
                log.info("unlock complete: {}", rLock.getName());
            }catch (IllegalMonitorStateException e){
                //이미 종료된 락일 때 발생하는 예외
                throw new IllegalArgumentException("Lock acquired");
            }
        }
    }
}
