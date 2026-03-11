package com.github.sa_cchu.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkedWarehouseDto {
    private Integer warehouseId;
    private String warehouseName;
    private String warehouseAddress;
}
