package com.github.sa_cchu.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.Goods;

public interface GoodsRepository extends JpaRepository<Goods, Integer> {
	Optional<Goods> findByGoodsName(String goodsName);

	@EntityGraph(attributePaths = "category")
	List<Goods> findByDeleteFlag(Integer deleteFlag);

	@EntityGraph(attributePaths = "category")
	List<Goods> findByCategoryAndDeleteFlag(Category category, Integer deleteFlag);

	Optional<Goods> findByGoodsNameAndDeleteFlag(String goodsName, int i);
	
	/**
	 * 新商品を追加する際に入力した商品名が存在するかどうかをチェックする。
	 * また、判定時に論理削除されていないもので判定する。
	 * @param goodsName 商品名
	 * @param deleteFlag 削除フラグ
	 * @return 判定結果（true/false）
	 */
    boolean existsByGoodsNameAndDeleteFlag(String goodsName, Integer deleteFlag);
	
}
