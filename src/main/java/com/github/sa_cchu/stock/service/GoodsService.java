package com.github.sa_cchu.stock.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;
// まとめてインポート
import com.github.sa_cchu.stock.repository.CategoryRepository;
import com.github.sa_cchu.stock.repository.GoodsRepository;
import com.github.sa_cchu.stock.repository.ShopRepository;
import com.github.sa_cchu.stock.repository.ShopStockRepository;
import com.github.sa_cchu.stock.repository.WarehouseRepository;
import com.github.sa_cchu.stock.repository.WarehouseStockRepository;

@Service
public class GoodsService {

	private final GoodsRepository goodsRepository;
	private final ShopRepository shopRepository;
	private final WarehouseRepository warehouseRepository;
	private final ShopStockRepository shopStockRepository;
	private final WarehouseStockRepository warehouseStockRepository;
	private final CategoryRepository categoryRepository;

	public GoodsService(GoodsRepository goodsRepository, ShopRepository shopRepository,
			WarehouseRepository warehouseRepository, ShopStockRepository shopStockRepository,
			WarehouseStockRepository warehouseStockRepository, CategoryRepository categoryRepository) {
		this.goodsRepository = goodsRepository;
		this.shopRepository = shopRepository;
		this.warehouseRepository = warehouseRepository;
		this.shopStockRepository = shopStockRepository;
		this.warehouseStockRepository = warehouseStockRepository;
		this.categoryRepository = categoryRepository;
	}

	/**
	 * プルダウン表示用の全カテゴリーを取得
	 */
	public List<Category> getAllActiveCategories() {
		return categoryRepository.findByDeleteFlag(0);
	}

	/**
	 * 商品一覧を取得（カテゴリーIDで絞り込み対応）
	 */
	public List<Goods> getGoodsList(Integer categoryId) {
		if (categoryId == null || categoryId == 0) {
			return goodsRepository.findByDeleteFlag(0);
		}

		Category category = new Category();
		category.setCategoryId(categoryId);
		return goodsRepository.findByCategoryAndDeleteFlag(category, 0);
	}

	/**
	 * 商品を新規登録し、同時に全店舗・全倉庫の在庫レコードを初期化します
	 */
	@Transactional // これが超重要！一連の処理をひとまとめにします
	public void saveGoods(Goods goods) {
		// 1. まずは商品を保存
		Goods savedGoods = goodsRepository.save(goods);

		// 2. 全店舗に対して、この商品の在庫レコード（0個）を作成
		List<Shop> allShops = shopRepository.findAll();
		for (Shop shop : allShops) {
			ShopStock shopStock = new ShopStock();
			shopStock.setShopId(shop);
			shopStock.setGoodsId(savedGoods);
			shopStock.setQuantity(0);
			shopStock.setDeleteFlag(0);
			shopStockRepository.save(shopStock);
		}

		// 3. 全倉庫に対して、この商品の在庫レコード（0個）を作成
		List<Warehouse> allWarehouses = warehouseRepository.findAll();
		for (Warehouse warehouse : allWarehouses) {
			WarehouseStock warehouseStock = new WarehouseStock();
			warehouseStock.setWarehouseId(warehouse);
			warehouseStock.setGoodsId(savedGoods);
			warehouseStock.setWarehouseStockQuantity(0);
			warehouseStock.setDeleteFlag(0);
			warehouseStockRepository.save(warehouseStock);
		}
	}
	
	@Transactional
	public void delelteGoods(Integer goodsId) {
		// 1. 商品の存在確認（念のため）
	    Goods goods = goodsRepository.findById(goodsId)
	            .orElseThrow(() -> new IllegalArgumentException("商品が存在しません"));

	    // 2. 店舗在庫を一括論理削除
	    shopStockRepository.bulkDeleteByGoodsId(goodsId);

	    // 3. 倉庫在庫を一括論理削除
	    warehouseStockRepository.bulkDeleteByGoodsId(goodsId);

	    // 4. 商品自体を論理削除
	    goods.setDeleteFlag(1);
	    goodsRepository.save(goods);
		
		
	}

}