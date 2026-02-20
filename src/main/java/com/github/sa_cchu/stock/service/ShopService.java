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

	@Transactional //全部成功でコミット　失敗でロールバック
	public List<Shop> getAllShop() {
		return shopRepository.findByDeleteFlag(0);
	}

	@Transactional
	public Shop addShop(Shop shop) {

		if (shopRepository.findByShopName(shop.getShopName()).isPresent()) {
			throw new IllegalArgumentException("既に店舗名は存在します");
		}
		// 店舗情報を保存
		Shop savedShop = shopRepository.save(shop);

		// 既存商品を取得
		List<Goods> allGoods = goodsRepository.findAll();

		// ShopStock作成と既存チェック
		for (Goods goods : allGoods) {//allGoodsに入っている商品をgoodsにいれて処理を回すfor文
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

	//編集する店舗ID取得
	@Transactional
	public Shop getShop(Integer shopId) {
		return shopRepository.findById(shopId).get();
	}

	//情報編集確認する画面
	@Transactional
	public void updateShop(Shop shop) {
		shopRepository.findByShopName(shop.getShopName())
				.ifPresent(exsiting -> {
					if (!exsiting.getShopId().equals(shop.getShopId())) {
						throw new IllegalArgumentException("既に店舗名は存在します");
					}
				});
		shopRepository.save(shop);
	}

	//論理削除
	@Transactional
	public void deleteShop(Integer shopId) {
		Shop shop = shopRepository.findById(shopId)
				.orElseThrow(() -> new IllegalArgumentException("店舗が存在しません"));
		shop.setDeleteFlag(1);
		shopRepository.save(shop);

	}

}
