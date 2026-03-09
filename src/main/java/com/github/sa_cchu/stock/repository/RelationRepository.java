package com.github.sa_cchu.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Relation;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.Warehouse;

public interface RelationRepository extends JpaRepository<Relation, Integer> {
	Optional<Relation> findByRelationId(Integer RelationId);

	List<Relation> findByDeleteFlag(Integer deleteFrag);
	List<Relation> findByShopAndDeleteFlag(Shop shop,Integer deleteFlag);
	
	
	    // 指定した店舗・倉庫で、削除されていない(deleteFlag=0)データがあるか確認
	    boolean existsByShopAndWarehouseAndDeleteFlag(Shop shop, Warehouse warehouse, Integer deleteFlag);

		
}

