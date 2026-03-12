package com.github.sa_cchu.stock.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.dto.RelationTargetDto;
import com.github.sa_cchu.stock.entity.Relation;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.User;
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

	/**
	 * 自分と連携している倉庫を取得するメソッド
	 * @param shopId
	 * @return 連携先のIDと名前のリスト
	 */
	public List<RelationTargetDto> getRelationWarehousesByShopId(Integer shopId) {
		//		if (shopId == null || shopId == 0) {
		//			return relationRepository.findByDeleteFlag(0);
		//		}
		Shop shop = new Shop();
		shop.setShopId(shopId);
		relationRepository.findByShopAndDeleteFlag(shop, 0);
		List<Relation> relations = relationRepository.findByShopAndDeleteFlag(shop, 0);

		return relations.stream()
				.map(r -> new RelationTargetDto(
						r.getWarehouse().getWarehouseId(),
						r.getWarehouse().getWarehouseName()))
				.toList();
	}

	/**
	 * 自分と連携している店舗を取得するメソッド
	 * @param warehouseId
	 * @return 連携先のIDと名前のリスト
	 */
	public List<RelationTargetDto> getRelationShopsByWarehouseId(Integer warehouseId) {
		//		if (warehouseId == null || warehouseId == 0) {
		//			return relationRepository.findByDeleteFlag(0);
		//		}

		Warehouse warehouse = new Warehouse();
		warehouse.setWarehouseId(warehouseId);
		List<Relation> relations = relationRepository.findByWarehouseAndDeleteFlag(warehouse, 0);

		return relations.stream()
				.map(r -> new RelationTargetDto(
						r.getShop().getShopId(),
						r.getShop().getShopName()))
				.toList();
	}

	/**
	 * 指定ユーザーの権限に応じて連携先（店舗 or 倉庫）を取得する
	 * @param user ログインユーザー
	 * @return 連携先のリスト
	 */
	public List<RelationTargetDto> getTargetsForUser(User user) {
		if (user.getShop() != null) {
			return getRelationWarehousesByShopId(user.getShop().getShopId());
		} else if (user.getWarehouse() != null) {
			return getRelationShopsByWarehouseId(user.getWarehouse().getWarehouseId());
		}
		return List.of();
	}

	@Transactional
	public void saveRelation(Relation relation) throws Exception {
		// 重複チェック (論理削除されていない有効なデータが対象)
		boolean exists = relationRepository.existsByShopAndWarehouseAndDeleteFlag(relation.getShop(),
				relation.getWarehouse(), 0);

		if (exists) {
			throw new Exception("この店舗と倉庫の組み合わせは既に登録されています。");
		}

		relationRepository.save(relation);
	}

	public Relation getRelationById(Integer id) {
		return relationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("指定された連携IDは存在しません"));
	}

	@Transactional
	public void deleteRelation(Integer relationId) {
		Relation relation = relationRepository.findById(relationId)
				.orElseThrow(() -> new IllegalArgumentException("連携記録がありません"));
		relation.setDeleteFlag(1);
		relationRepository.save(relation);

	}

}
