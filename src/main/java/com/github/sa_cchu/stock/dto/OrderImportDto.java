package com.github.sa_cchu.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderImportDto {

 
    private String goodsName;
    private String warehouseName;
    private String amount;

}