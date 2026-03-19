package com.github.sa_cchu.stock.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import jakarta.transaction.Transactional;

import com.github.sa_cchu.stock.dto.ShopStockDto;
import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;
import com.github.sa_cchu.stock.repository.ShopStockRepository;
import java.util.stream.Collectors;

@Service
public class ShopStockService {
	private final ShopStockRepository shopStockRepository;

	public ShopStockService(ShopStockRepository shopStockRepository) {
		this.shopStockRepository = shopStockRepository;
	}

	public List<ShopStockDto> getShopStockDtoList(Shop shop, Integer categoryId) {
		List<ShopStock> entityList;

		if (categoryId == null || categoryId == 0) {
			entityList = shopStockRepository.findByShopIdAndDeleteFlag(shop, 0);
		} else {
			Category category = new Category();
			category.setCategoryId(categoryId);
			entityList = shopStockRepository.findByShopIdAndGoodsIdCategoryAndDeleteFlag(shop, category, 0);
		}

		return entityList.stream().map(entity -> {
			Integer shopStockId = entity.getShopStockId();
			String shopName = entity.getShopId().getShopName();
			String categoryName = entity.getGoodsId().getCategory().getCategoryName();
			String goodsName = entity.getGoodsId().getGoodsName();
			Integer shopStockQuantity = entity.getShopStockQuantity();

			return new ShopStockDto(shopStockId, shopName, categoryName, goodsName, shopStockQuantity);
		}).collect(Collectors.toList());
	}

	public ShopStock getShopStock(Integer shopStockId, Shop userShop) {
		ShopStock stock = shopStockRepository.findById(shopStockId)
				.orElseThrow(() -> new IllegalArgumentException("在庫が見つかりません"));

		// IDOR対策 他倉庫の在庫を見れないように実装。
		// 操作しようとしている在庫が、ログインユーザーの倉庫と同じかチェックします。
		if (!stock.getShopId().getShopId().equals(userShop.getShopId())) {
			throw new AccessDeniedException("他の店舗の在庫にアクセスする権限がありません。");
		}

		return stock;
	}

	@Transactional
	public void updateStockQuantity(Integer shopStockId, Integer quantity, Shop userShop) {
		ShopStock stock = getShopStock(shopStockId, userShop);
		stock.setShopStockQuantity(quantity);
		shopStockRepository.save(stock);
	}
}