package com.github.sa_cchu.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Shop;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
	Optional<Shop> findByShopName(String shopName);

}
