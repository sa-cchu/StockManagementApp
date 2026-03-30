package com.github.sa_cchu.stock.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.sa_cchu.stock.entity.Orders;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.Warehouse;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Orders o WHERE o.orderId = :orderId")
    Optional<Orders> findByIdForUpdate(@Param("orderId") Integer orderId);

    // 店舗ごとの発注一覧（動的絞り込み）
    @EntityGraph(attributePaths = {"goods", "warehouse"})
    @Query("SELECT o FROM Orders o " +
           "WHERE o.shop = :shop AND o.deleteFlag = :deleteFlag " +
           "AND (:orderStatus IS NULL OR :orderStatus = '' OR o.orderStatus = :orderStatus) " +
           "AND (cast(:startDate as date) IS NULL OR o.orderDate >= :startDate) " +
           "AND (cast(:endDate as date) IS NULL OR o.orderDate <= :endDate) " +
           "ORDER BY o.orderDate DESC")
    List<Orders> findByShopWithFilters(@Param("shop") Shop shop, 
                                       @Param("orderStatus") String orderStatus, 
                                       @Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate, 
                                       @Param("deleteFlag") Integer deleteFlag);

    long countByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    // 倉庫ごとの受注一覧（動的絞り込み）
    @EntityGraph(attributePaths = {"goods", "shop"})
    @Query("SELECT o FROM Orders o " +
           "WHERE o.warehouse = :warehouse AND o.deleteFlag = :deleteFlag " +
           "AND (:orderStatus IS NULL OR :orderStatus = '' OR o.orderStatus = :orderStatus) " +
           "AND (cast(:startDate as date) IS NULL OR o.orderDate >= :startDate) " +
           "AND (cast(:endDate as date) IS NULL OR o.orderDate <= :endDate) " +
           "ORDER BY o.orderDate DESC")
    List<Orders> findByWarehouseWithFilters(@Param("warehouse") Warehouse warehouse, 
                                            @Param("orderStatus") String orderStatus, 
                                            @Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate, 
                                            @Param("deleteFlag") Integer deleteFlag);
}
