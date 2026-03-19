package com.github.sa_cchu.stock.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.sa_cchu.stock.dto.OrderHistoryDto;
import com.github.sa_cchu.stock.entity.Orders;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.repository.OrdersRepository;

@Service
public class WarehouseOrderService {

    private final OrdersRepository ordersRepository;

    public WarehouseOrderService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    // 受注一覧取得（ステータス＋期間絞り込み対応）
    public List<OrderHistoryDto> getOrderHistoryList(Warehouse warehouse, String status, LocalDateTime startDate, LocalDateTime endDate) {
        List<Orders> ordersList = ordersRepository.findByWarehouseWithFilters(warehouse, status, startDate, endDate, 0);

        return ordersList.stream().map(order -> {
            OrderHistoryDto dto = new OrderHistoryDto();
            dto.setOrderId(order.getOrderId());
            dto.setGoodsName(order.getGoods().getGoodsName());
            dto.setShopName(order.getShop().getShopName());
            dto.setOrderAmount(order.getOrderAmount());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setOrderDate(order.getOrderDate());
            dto.setUpdateDate(order.getUpdateDate());
            return dto;
        }).collect(Collectors.toList());
    }

    // ステータス更新
    @Transactional
    public void updateOrderStatus(Integer orderId, Warehouse warehouse) throws Exception {
        Orders order = ordersRepository.findById(orderId).orElse(null);

        if (order == null) {
            throw new Exception("発注データが見つかりません。");
        }
        // 自分の倉庫の受注かチェック（セキュリティ対策）
        if (!order.getWarehouse().getWarehouseId().equals(warehouse.getWarehouseId())) {
            throw new Exception("この受注を変更する権限がありません。");
        }
        // 既に発送済みなら変更不可
        if ("発送済み".equals(order.getOrderStatus())) {
            throw new Exception("既に発送済みの受注です。");
        }

        order.setOrderStatus("発送済み");
        order.setUpdateDate(LocalDateTime.now());
        ordersRepository.save(order);
    }
}
