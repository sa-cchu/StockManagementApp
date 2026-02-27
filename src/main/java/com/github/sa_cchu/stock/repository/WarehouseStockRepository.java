package com.github.sa_cchu.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Integer> {

	boolean existsByWarehouseIdAndGoodsId(Warehouse warehouse, Goods goods);

	@Modifying(clearAutomatically = true)//「データの変更（INSERT, UPDATE, DELETE）」 であることを明示
	@Query("UPDATE WarehouseStock w SET w.deleteFlag = 1 WHERE w.goodsId.goodsId = :goodsId")
	void bulkDeleteByGoodsId(@Param("goodsId") Integer goodsId);
}
