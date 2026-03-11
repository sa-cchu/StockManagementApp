package com.github.sa_cchu.stock.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.dto.LinkedWarehouseDto;
import com.github.sa_cchu.stock.entity.Relation;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.repository.RelationRepository;

@Service
public class LinkedWarehouseService {

	private final RelationRepository relationRepository;

	public LinkedWarehouseService(RelationRepository relationRepository) {
		this.relationRepository = relationRepository;
	}

	public List<LinkedWarehouseDto> getLinkedWarehouses(Shop shop) {
		List<Relation> relations = relationRepository.findByShopAndDeleteFlag(shop, 0);
		return relations.stream().map(relation -> new LinkedWarehouseDto(
				relation.getWarehouse().getWarehouseId(),
				relation.getWarehouse().getWarehouseName(),
				relation.getWarehouse().getWarehouseAddress()))
				.toList();
	}
}
