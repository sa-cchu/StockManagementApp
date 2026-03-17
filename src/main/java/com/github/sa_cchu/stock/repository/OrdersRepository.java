package com.github.sa_cchu.stock.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Orders;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.Warehouse;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
	List<Orders> findByShopAndDeleteFlagOrderByOrderDateDesc(Shop shop, Integer deleteFlag);

	long countByOrderDateBetween(LocalDateTime start, LocalDateTime end);

	List<Orders> findByWarehouseAndDeleteFlagOrderByOrderDateDesc(Warehouse warehouse, Integer deleteFlag);

	List<Orders> findByWarehouseAndOrderStatusAndDeleteFlagOrderByOrderDateDesc(Warehouse warehouse, String orderStatus, Integer deleteFlag);
}
