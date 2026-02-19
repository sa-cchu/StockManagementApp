package com.github.sa_cchu.stock.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;
import com.github.sa_cchu.stock.repository.GoodsRepository;
import com.github.sa_cchu.stock.repository.ShopRepository;
import com.github.sa_cchu.stock.repository.ShopStockRepository;

@Service
public class ShopService {

	private final ShopRepository shopRepository;
	private final GoodsRepository goodsRepository;
	private final ShopStockRepository shopStockRepository;

	public ShopService(ShopRepository shopRepository,
			GoodsRepository goodsRepository,
			ShopStockRepository shopStockRepository) {
		this.shopRepository = shopRepository;
		this.goodsRepository = goodsRepository;
		this.shopStockRepository = shopStockRepository;
	}

	@Transactional//全部成功でコミット　失敗でロールバック
	public List<Shop> getAllShop() {
		return shopRepository.findAll();
	}

	@Transactional
	public Shop addShop(Shop shop) {

		// 店舗情報を保存
		Shop savedShop = shopRepository.save(shop);

		// 既存商品を取得
		List<Goods> allGoods = goodsRepository.findAll();

		// ShopStock作成と既存チェック
		for (Goods goods : allGoods) {
			boolean exists = shopStockRepository.existsByShopIdAndGoodsId(savedShop, goods); //
			if (!exists) {
				ShopStock stock = new ShopStock();
				stock.setShopId(savedShop);
				stock.setGoodsId(goods);
				stock.setQuantity(0);
				shopStockRepository.save(stock);
			}
		}
		return savedShop;
	}

}
