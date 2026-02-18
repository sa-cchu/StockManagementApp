package com.github.sa_cchu.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Goods;

public interface GoodsRepository extends JpaRepository<Goods, Integer> {
	Optional<Goods> findByGoodsName(String goodsName);
}
