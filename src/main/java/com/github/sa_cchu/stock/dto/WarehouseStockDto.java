package com.github.sa_cchu.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseStockDto {
    private Integer warehouseStockId;
    private String warehouseName;
    private String categoryName;
    private String goodsName;
    private Integer warehouseStockQuantity;
}
