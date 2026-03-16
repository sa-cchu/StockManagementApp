package com.github.sa_cchu.stock.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderHistoryDto {
    private Integer orderId;          // 発注ID
    private String goodsName;         // 商品名
    private String shopName;          // 発注元店舗名
    private String warehouseName;     // 発注先倉庫名
    private Integer orderAmount;      // 発注個数
    private String orderStatus;       // ステータス
    private LocalDateTime orderDate;  // 発注日時
    private LocalDateTime updateDate; // 更新日時
}