package com.github.sa_cchu.stock.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.sa_cchu.stock.dto.GoodsRankingDTO;
import com.github.sa_cchu.stock.repository.DailyOrderSummaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
	private final DailyOrderSummaryRepository dailyOrderSummaryRepository;

	@Scheduled(cron = "0 10 0 * * *")
	@Transactional
	public void aggregDailyOrders() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDateTime start = yesterday.atStartOfDay();
		LocalDateTime end = LocalDate.now().atStartOfDay();

		try {
			log.info("{}の集計バッチを開始します。", yesterday);
			dailyOrderSummaryRepository.deleteByCountDate(yesterday);
			dailyOrderSummaryRepository.insertDailySummary(start, end);

			log.info("{}の集計バッチが正常に完了しました。。", yesterday);

		} catch (Exception e) {
			log.error("{}のバッチ処理中にエラーが発生しました。", yesterday, e);
		}
	}
	//商品ランキング
	public List<GoodsRankingDTO> getAllShopRanking(){
		LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
		List<Object[]> results = dailyOrderSummaryRepository.findTopGoods(oneMonthAgo); 
		return convertToDto(results);
	}
	
	//所属店舗所品ランキング
	public List<GoodsRankingDTO>getMyShopRanking(Integer shopId){
		LocalDate oneMonthAgoDate = LocalDate.now().minusMonths(1);
		List<Object[]> results =dailyOrderSummaryRepository.findTopGoodsByShop(shopId, oneMonthAgoDate);
		return convertToDto(results);
	}
	
	
	// Dtoに変換する
	private List<GoodsRankingDTO> convertToDto(List<Object[]> results) {
	    return results.stream()
	            .map(result -> new GoodsRankingDTO(
	                (String) result[0],              // 商品名
	                ((Number) result[1]).intValue()  // 合計数
	            ))
	            .collect(Collectors.toList());
	}
	
	
}
