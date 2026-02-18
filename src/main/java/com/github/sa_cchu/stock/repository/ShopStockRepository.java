package com.github.sa_cchu.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;

public interface ShopStockRepository extends JpaRepository<ShopStock, Integer> {
    // ShopStock のフィールド名に合わせる
    boolean existsByShopIdAndGoodsId(Shop shop, Goods goods);
}
