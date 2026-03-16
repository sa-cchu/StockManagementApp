package com.github.sa_cchu.stock.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.github.sa_cchu.stock.entity.DailyOrderSummary;

public interface DailyOrderSummaryRepository extends JpaRepository<DailyOrderSummary, Integer> {
	
	@Modifying
	@Transactional
	void deleteByCountDate(LocalDate countDate);

	@Modifying
	@Transactional
	@Query(value = """
			INSERT INTO daily_order_summary (count_date, shop_id, goods_id, goods_amount)
			SELECT CAST(order_date AS DATE), shop_id, goods_id, SUM(order_amount)
			FROM orders
			WHERE order_date >= :targetDate AND order_date < :nextDate
			  AND delete_flag = 0
			GROUP BY CAST(order_date AS DATE), shop_id, goods_id
			""", nativeQuery = true)
	void insertDailySummary(LocalDateTime targetDate, LocalDateTime nextDate);

	// 全店舗商品ランキング
	@Query(value = """
			SELECT g.goods_name, SUM(s.goods_amount) as total
			FROM daily_order_summary s
			JOIN goods g ON s.goods_id = g.goods_id
			WHERE s.count_date >= :startDate
			GROUP BY g.goods_id, g.goods_name
			ORDER BY total DESC
			LIMIT 5
			""", nativeQuery = true)
	List<Object[]> findTopGoods(LocalDate startDate);

	// 所属店舗の商品ランキング
	@Query(value = """
			SELECT g.goods_name, SUM(s.goods_amount) as total
			FROM daily_order_summary s
			JOIN goods g ON s.goods_id = g.goods_id
			WHERE s.shop_id = :shopId AND s.count_date >= :startDate
			GROUP BY g.goods_id, g.goods_name
			ORDER BY total DESC
			LIMIT 5
			""", nativeQuery = true)
	List<Object[]> findTopGoodsByShop(Integer shopId, LocalDate startDate);
}
