package com.github.sa_cchu.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;


public interface ShopStockRepository extends JpaRepository<ShopStock, Integer> {
    // ShopStock のフィールド名に合わせる
    boolean existsByShopIdAndGoodsId(Shop shop, Goods goods);
   
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ShopStock s SET s.deleteFlag = 1 WHERE s.goodsId.goodsId = :goodsId")
    void bulkDeleteByGoodsId(@Param("goodsId") Integer goodsId);
    
	List<ShopStock> findByShopIdAndDeleteFlag(Shop shop, Integer deleteFlag);

	List<ShopStock> findByShopIdAndGoodsIdCategoryAndDeleteFlag(Shop shop, Category category, Integer deleteFlag);
}
