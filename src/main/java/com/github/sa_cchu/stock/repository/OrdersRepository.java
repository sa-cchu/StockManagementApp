package com.github.sa_cchu.stock.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.github.sa_cchu.stock.entity.Orders;
import com.github.sa_cchu.stock.entity.Shop;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByShopAndDeleteFlagOrderByOrderDateDesc(Shop shop, Integer deleteFlag);
}
