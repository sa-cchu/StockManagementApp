package com.github.sa_cchu.stock.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Orders;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.Warehouse;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    
    // 店舗ごとの発注一覧（常に Goods と Warehouse を JOIN FETCH する）
    @EntityGraph(attributePaths = {"goods", "warehouse"})
    List<Orders> findByShopAndDeleteFlagOrderByOrderDateDesc(Shop shop, Integer deleteFlag);

    @EntityGraph(attributePaths = {"goods", "warehouse"})
    List<Orders> findByShopAndOrderStatusAndDeleteFlagOrderByOrderDateDesc(Shop shop, String orderStatus, Integer deleteFlag);

    @EntityGraph(attributePaths = {"goods", "warehouse"})
    List<Orders> findByShopAndOrderDateBetweenAndDeleteFlagOrderByOrderDateDesc(Shop shop, LocalDateTime start, LocalDateTime end, Integer deleteFlag);

    @EntityGraph(attributePaths = {"goods", "warehouse"})
    List<Orders> findByShopAndOrderStatusAndOrderDateBetweenAndDeleteFlagOrderByOrderDateDesc(Shop shop, String orderStatus, LocalDateTime start, LocalDateTime end, Integer deleteFlag);

    long countByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    // 倉庫ごとの受注一覧（常に Goods と Shop を JOIN FETCH する）
    @EntityGraph(attributePaths = {"goods", "shop"})
    List<Orders> findByWarehouseAndDeleteFlagOrderByOrderDateDesc(Warehouse warehouse, Integer deleteFlag);

    @EntityGraph(attributePaths = {"goods", "shop"})
    List<Orders> findByWarehouseAndOrderStatusAndDeleteFlagOrderByOrderDateDesc(Warehouse warehouse, String orderStatus, Integer deleteFlag);

    @EntityGraph(attributePaths = {"goods", "shop"})
    List<Orders> findByWarehouseAndOrderDateBetweenAndDeleteFlagOrderByOrderDateDesc(Warehouse warehouse, LocalDateTime start, LocalDateTime end, Integer deleteFlag);

    @EntityGraph(attributePaths = {"goods", "shop"})
    List<Orders> findByWarehouseAndOrderStatusAndOrderDateBetweenAndDeleteFlagOrderByOrderDateDesc(Warehouse warehouse, String orderStatus, LocalDateTime start, LocalDateTime end, Integer deleteFlag);
}
