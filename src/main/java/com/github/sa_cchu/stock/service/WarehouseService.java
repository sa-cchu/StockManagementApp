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
	//一覧表示
	@Transactional
	public List<Warehouse> getAllWarehouses() {
		return warehouseRepository.findByDeleteFlag(0);
	}

	//倉庫追加
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

	//編集する倉庫取得
	@Transactional
	public Warehouse getWarehouse(Integer warehouseId) {
		return warehouseRepository.findById(warehouseId).get();
	}

	@Transactional
	public void updateWarehouse(Warehouse warehouse) {

		warehouseRepository.findByWarehouseName(warehouse.getWarehouseName())
			.ifPresent(existing -> {
				if (!existing.getWarehouseId().equals(warehouse.getWarehouseId())) {
					throw new IllegalArgumentException("既に倉庫名は存在しています");
				}
			});

		warehouseRepository.save(warehouse);
	}
	
	
	// 論理削除
	@Transactional
	public void deleteWarehouse(Integer warehouseId) {
		Warehouse warehouse = warehouseRepository.findById(warehouseId)
				.orElseThrow(()->new IllegalArgumentException("倉庫が存在しません"));
		warehouse.setDeleteFlag(1);
		warehouseRepository.save(warehouse);
	}

}
