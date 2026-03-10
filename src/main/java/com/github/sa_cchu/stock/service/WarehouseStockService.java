package com.github.sa_cchu.stock.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import jakarta.transaction.Transactional;

import com.github.sa_cchu.stock.dto.WarehouseStockDto;
import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;
import com.github.sa_cchu.stock.repository.WarehouseStockRepository;
import java.util.stream.Collectors;

@Service
public class WarehouseStockService {
	private final WarehouseStockRepository warehouseStockRepository;

	public WarehouseStockService(WarehouseStockRepository warehouseStockRepository) {
		this.warehouseStockRepository = warehouseStockRepository;
	}

	public List<WarehouseStockDto> getWarehouseStockDtoList(Warehouse warehouse, Integer categoryId) {
		List<WarehouseStock> entityList;

		if (categoryId == null || categoryId == 0) {
			entityList = warehouseStockRepository.findByWarehouseIdAndDeleteFlag(warehouse, 0);
		} else {
			Category category = new Category();
			category.setCategoryId(categoryId);
			entityList = warehouseStockRepository.findByWarehouseIdAndGoodsIdCategoryAndDeleteFlag(warehouse, category, 0);
		}

		return entityList.stream().map(entity -> {
			Integer stockId = entity.getWarehouseStockId();
			String warehouseName = entity.getWarehouseId().getWarehouseName();
			String categoryName = entity.getGoodsId().getCategory().getCategoryName();
			String goodsName = entity.getGoodsId().getGoodsName();
			Integer quantity = entity.getWarehouseStockQuantity();

			return new WarehouseStockDto(stockId, warehouseName, categoryName, goodsName, quantity);
		}).collect(Collectors.toList());
	}

	public WarehouseStock getWarehouseStock(Integer warehouseStockId, Warehouse userWarehouse) {
		WarehouseStock stock = warehouseStockRepository.findById(warehouseStockId)
				.orElseThrow(() -> new IllegalArgumentException("在庫が見つかりません"));

		// IDOR対策 他倉庫の在庫を見れないように実装。
		// 操作しようとしている在庫が、ログインユーザーの倉庫と同じかチェックします。
		if (!stock.getWarehouseId().getWarehouseId().equals(userWarehouse.getWarehouseId())) {
			throw new AccessDeniedException("他の倉庫の在庫にアクセスする権限がありません。");
		}

		return stock;
	}

	@Transactional
	public void updateStockQuantity(Integer warehouseStockId, Integer quantity, Warehouse userWarehouse) {
		WarehouseStock stock = getWarehouseStock(warehouseStockId, userWarehouse);
		stock.setWarehouseStockQuantity(quantity);
		warehouseStockRepository.save(stock);
	}
}