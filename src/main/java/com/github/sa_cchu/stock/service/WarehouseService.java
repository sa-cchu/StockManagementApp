package com.github.sa_cchu.stock.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;
import com.github.sa_cchu.stock.repository.GoodsRepository;
import com.github.sa_cchu.stock.repository.WarehouseRepository;
import com.github.sa_cchu.stock.repository.WarehouseStockRepository;

@Service
public class WarehouseService {

	private final WarehouseRepository warehouseRepository;
	private final GoodsRepository goodsRepository;
	private final WarehouseStockRepository warehouseStockRepository;

	public WarehouseService(WarehouseRepository warehouseRepository,
			GoodsRepository goodsRepository, WarehouseStockRepository warehouseStockRepository) {
		this.warehouseRepository = warehouseRepository;
		this.goodsRepository = goodsRepository;
		this.warehouseStockRepository = warehouseStockRepository;
	}

	//リポジトリの操作でエンティティから倉庫データを全部もってきている
	@Transactional
	public List<Warehouse> getAllWarehouses() {
		return warehouseRepository.findAll();
	}

	@Transactional
	public Warehouse addWarehouse(Warehouse warehouse) {

		if (warehouseRepository.findByWarehouseName(warehouse.getWarehouseName()).isPresent()) {
			throw new IllegalArgumentException("既に倉庫名は存在しています");
		}

		//リポジトリのメソッドを呼んで受け取った値を変数に代入
		Warehouse saveWarehouse = warehouseRepository.save(warehouse);
		List<Goods> allGoods = goodsRepository.findAll();

		for (Goods goods : allGoods) {
			boolean exsits = warehouseStockRepository.existsByWarehouseIdAndGoodsId(saveWarehouse, goods);

			if (!exsits) {
				WarehouseStock stock = new WarehouseStock();
				stock.setWarehouseId(saveWarehouse);
				stock.setGoodsId(goods);
				stock.setWarehouseStockQuantity(0);
				warehouseStockRepository.save(stock);
			}
		}
		return saveWarehouse;
	}

}
