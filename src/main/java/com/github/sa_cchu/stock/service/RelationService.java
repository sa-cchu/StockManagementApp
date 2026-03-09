package com.github.sa_cchu.stock.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.entity.Relation;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.repository.RelationRepository;
import com.github.sa_cchu.stock.repository.ShopRepository;
import com.github.sa_cchu.stock.repository.WarehouseRepository;

@Service
public class RelationService {

	private final WarehouseRepository warehouseRepository;

	private final ShopRepository shopRepository;

	private final RelationRepository relationRepository;

	public RelationService(RelationRepository relationRepository, ShopRepository shopRepository,
			WarehouseRepository warehouseRepository) {
		this.relationRepository = relationRepository;
		this.shopRepository = shopRepository;
		this.warehouseRepository = warehouseRepository;
	}

	// プルダウン用一覧
	public List<Shop> getAllActiveShops() {
		return shopRepository.findByDeleteFlag(0);
	}

	public List<Warehouse> getAllActivesWarehouses() {
		return warehouseRepository.findByDeleteFlag(0);
	}

	public List<Relation> getRelationList(Integer shopId) {
		if (shopId == null || shopId == 0) {
			return relationRepository.findByDeleteFlag(0);
		}

		Shop shop = new Shop();
		shop.setShopId(shopId);
		return relationRepository.findByShopAndDeleteFlag(shop, 0);

	}

	
	@Transactional
	public void saveRelation(Relation relation) throws Exception {
	    // 重複チェック (論理削除されていない有効なデータが対象)
	    boolean exists = relationRepository.existsByShopAndWarehouseAndDeleteFlag(
	        relation.getShop(), 
	        relation.getWarehouse(), 
	        0
	    );

	    if (exists) {
	        throw new Exception("この店舗と倉庫の組み合わせは既に登録されています。");
	    }

	    relationRepository.save(relation);
	}

}
