package com.github.sa_cchu.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse,Integer>{
	
	Optional<Warehouse> findByWarehouseName(String warehouseNamw);
}
