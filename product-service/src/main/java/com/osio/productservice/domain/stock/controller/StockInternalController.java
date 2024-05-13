package com.osio.productservice.domain.stock.controller;

import com.osio.productservice.domain.client.order.dto.ProductReqDto;
import com.osio.productservice.domain.client.order.dto.StockReqDto;
import com.osio.productservice.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/internal/stock")
@RequiredArgsConstructor
public class StockInternalController {

    private final StockService stockService;

    // redis 재고 감소
    @PostMapping("/decrease")
    boolean decreaseStockQuantity(@RequestBody StockReqDto.StockQuantityDto stockQuantityDto) throws Exception {
        log.info("StockInternalController.decreaseStockQuantity()");

        return stockService.decreaseStockQuantity(stockQuantityDto);
    }

    // redis 재고 복구
    @PostMapping("/increase")
    void increaseStockQuantity(@RequestBody StockReqDto.StockQuantityDto stockQuantityDto) {
        log.info("StockInternalController.increaseStockQuantity()");

        stockService.increaseStockQuantity(stockQuantityDto);
    }
}
