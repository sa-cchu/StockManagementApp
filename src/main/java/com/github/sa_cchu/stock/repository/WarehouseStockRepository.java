package com.github.sa_cchu.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Integer>{
	
	boolean existsByWarehouseIdAndGoodsId(Warehouse warehouse, Goods goods);
}
