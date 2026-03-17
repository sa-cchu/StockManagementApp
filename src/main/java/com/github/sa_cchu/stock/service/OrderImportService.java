package com.github.sa_cchu.stock.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.sa_cchu.stock.dto.OrderImportDto;
import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Orders;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;
import com.github.sa_cchu.stock.repository.GoodsRepository;
import com.github.sa_cchu.stock.repository.OrdersRepository;
import com.github.sa_cchu.stock.repository.RelationRepository;
import com.github.sa_cchu.stock.repository.ShopStockRepository;
import com.github.sa_cchu.stock.repository.WarehouseRepository;
import com.github.sa_cchu.stock.repository.WarehouseStockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderImportService {

	private final GoodsRepository goodsRepository;
	private final WarehouseRepository warehouseRepository;
	private final RelationRepository relationRepository;
	private final WarehouseStockRepository warehouseStockRepository;
	private final ShopStockRepository shopStockRepository;
	private final OrdersRepository ordersRepository;

	/**
	 * Excelファイルを解析してDTOリストに変換
	 */
	public List<OrderImportDto> parseExcel(MultipartFile file) throws Exception {
		List<OrderImportDto> dtoList = new ArrayList<>();

		try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
			// 全シートをループで回す
			for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
				Sheet sheet = workbook.getSheetAt(s);
				System.out.println("シート名: " + sheet.getSheetName() + " を処理中...");

				int lastRow = sheet.getLastRowNum();
				for (int i = 1; i <= lastRow; i++) { // 各シート1行目はヘッダーと想定
					Row row = sheet.getRow(i);
					if (row == null || isRowEmpty(row))
						continue;

					OrderImportDto dto = new OrderImportDto(getCellValue(row.getCell(0)), // A列
							getCellValue(row.getCell(1)), // B列
							getCellValue(row.getCell(2)) // C列
					);
					dtoList.add(dto);
				}
			}
		}
		System.out.println("全シート合計のインポート件数: " + dtoList.size());
		return dtoList;
	}

	/**
	 * 一括インポート実行
	 * 
	 * @param sessionShop セッションから取得したログイン店舗
	 * @throws Exception 1件でもエラーがあれば例外を投げ、全ロールバックする
	 */
	@Transactional(rollbackFor = Exception.class)
	public void executeImport(Shop shop, List<OrderImportDto> dtoList) throws Exception {
		for (int i = 0; i < dtoList.size(); i++) {
			OrderImportDto dto = dtoList.get(i);
			int rowNum = i + 2; // Excel上の行番号（index 0から数えてヘッダー含め+2）

			try {
				// 1. マスタ存在チェック（名前から検索）
				Goods goods = goodsRepository.findByGoodsNameAndDeleteFlag(dto.getGoodsName(), 0)
						.orElseThrow(() -> new Exception("商品名「" + dto.getGoodsName() + "」が存在しません。"));

				Warehouse warehouse = warehouseRepository.findByWarehouseNameAndDeleteFlag(dto.getWarehouseName(), 0)
						.orElseThrow(() -> new Exception("倉庫名「" + dto.getWarehouseName() + "」が存在しません。"));

				// 2. 数値変換・バリデーション
				int amount;
				try {
					amount = Integer.parseInt(dto.getAmount());
					if (amount <= 0)
						throw new Exception("数量は1以上で入力してください。");
				} catch (NumberFormatException e) {
					throw new Exception("数量「" + dto.getAmount() + "」は数値で入力してください。");
				}

				// 3. 連携チェック
				if (!relationRepository.existsByShopAndWarehouseAndDeleteFlag(shop, warehouse, 0)) {
					throw new Exception("倉庫「" + warehouse.getWarehouseName() + "」とは連携していません。");
				}

				// 4. 倉庫在庫チェック
				WarehouseStock wStock = warehouseStockRepository.findByWarehouseIdAndGoodsIdAndDeleteFlag(warehouse,
						goods, 0);
				if (wStock == null || wStock.getWarehouseStockQuantity() < amount) {
					throw new Exception("倉庫に十分な在庫がありません。");
				}

				// 5. 各種テーブル更新
				// 倉庫在庫減算
				wStock.setWarehouseStockQuantity(wStock.getWarehouseStockQuantity() - amount);
				warehouseStockRepository.save(wStock);

				// 発注レコード作成
				Orders order = new Orders();
				order.setShop(shop);
				order.setGoods(goods);
				order.setWarehouse(warehouse);
				order.setOrderAmount(amount);
				order.setOrderStatus("準備中");
				order.setOrderDate(LocalDateTime.now());
				order.setUpdateDate(LocalDateTime.now());
				order.setDeleteFlag(0);
				ordersRepository.save(order);

				// 店舗在庫加算
				ShopStock sStock = shopStockRepository.findByShopIdAndGoodsIdAndDeleteFlag(shop, goods, 0);
				if (sStock == null)
					throw new Exception("店舗在庫データが未登録です。");
				sStock.setShopStockQuantity(sStock.getShopStockQuantity() + amount);
				shopStockRepository.save(sStock);

			} catch (Exception e) {
				// エラー内容に行番号を付与して上位(Controller)へ投げる
				throw new Exception(rowNum + "行目: " + e.getMessage());
			}
		}
	}

	private String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		String val = switch (cell.getCellType()) {
		case STRING -> cell.getStringCellValue();
		case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
		default -> "";
		};
		return val.trim();
	}

	private boolean isRowEmpty(Row row) {
		if (row == null)
			return true;
		Cell cell = row.getCell(0);
		return cell == null || cell.getCellType() == CellType.BLANK;
	}
}