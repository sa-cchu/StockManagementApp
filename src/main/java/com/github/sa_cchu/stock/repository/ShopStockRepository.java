package com.github.sa_cchu.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;


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
    
	@EntityGraph(attributePaths = {"goodsId", "shopId", "goodsId.category"})
	List<ShopStock> findByShopIdAndDeleteFlag(Shop shop, Integer deleteFlag);

	@EntityGraph(attributePaths = {"goodsId", "shopId", "goodsId.category"})
	List<ShopStock> findByShopIdAndGoodsIdCategoryAndDeleteFlag(Shop shop, Category category, Integer deleteFlag);

	@EntityGraph(attributePaths = {"goodsId", "shopId"})
	ShopStock findByShopIdAndGoodsIdAndDeleteFlag(Shop shop, Goods goods, Integer deleteFlag);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT ss FROM ShopStock ss WHERE ss.shopId = :shop AND ss.goodsId = :goods AND ss.deleteFlag = :deleteFlag")
	ShopStock findForUpdate(@Param("shop") Shop shop, @Param("goods") Goods goods, @Param("deleteFlag") Integer deleteFlag);
}
