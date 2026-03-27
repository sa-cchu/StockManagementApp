package com.github.sa_cchu.stock.service;

import java.io.ByteArrayOutputStream; // これも必要です
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // .xlsx形式を扱う場合に必要
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.sa_cchu.stock.dto.OrderImportDto;
import com.github.sa_cchu.stock.entity.Category;
import com.github.sa_cchu.stock.entity.Goods;
import com.github.sa_cchu.stock.entity.Orders;
import com.github.sa_cchu.stock.entity.Shop;
import com.github.sa_cchu.stock.entity.ShopStock;
import com.github.sa_cchu.stock.entity.Warehouse;
import com.github.sa_cchu.stock.entity.WarehouseStock;
import com.github.sa_cchu.stock.repository.CategoryRepository;
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
	private final OrdersRepository ordersRepository;
	private final CategoryRepository categoryRepository;
	private final ShopStockRepository shopStockRepository;
	private final WarehouseStockRepository warehouseStockRepository;

	/**
	 * Excelファイルを解析してDTOリストに変換
	 */
	public List<OrderImportDto> parseExcel(MultipartFile file) throws Exception {
	    List<OrderImportDto> dtoList = new ArrayList<>();
	    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
	        for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
	            Sheet sheet = workbook.getSheetAt(s);
	            int lastRow = sheet.getLastRowNum();

	            for (int i = 1; i <= lastRow; i++) {
	                Row row = sheet.getRow(i);
	                if (row == null || isRowEmpty(row)) continue;

	                // DTO作成時に「シート名」と「実際の行番号(i+1)」を覚えさせる
	                OrderImportDto dto = new OrderImportDto(
	                    getCellValue(row.getCell(0)),
	                    getCellValue(row.getCell(1)),
	                    getCellValue(row.getCell(2)),
	                    sheet.getSheetName(),
	                    i + 1
	                );
	                dtoList.add(dto);
	            }
	        }
	    }
	    return dtoList;
	}

	@Transactional(rollbackFor = Exception.class)
	public void executeImport(Shop shop, List<OrderImportDto> dtoList) throws Exception {
	    List<String> errorMessages = new ArrayList<>();

	    for (OrderImportDto dto : dtoList) {
	        try {
	            // 1. 入力値の基本チェック（空行スキップとトリム）
	            if (dto.getAmount() == null || dto.getAmount().isBlank()) continue;
	            String gName = dto.getGoodsName().trim();
	            String wName = dto.getWarehouseName().trim();

	            // 2. マスタ存在チェック
	            Goods goods = goodsRepository.findByGoodsNameAndDeleteFlag(gName, 0)
	                    .orElseThrow(() -> new Exception("商品「" + gName + "」がマスタに存在しません。"));

	            Warehouse warehouse = warehouseRepository.findByWarehouseNameAndDeleteFlag(wName, 0)
	                    .orElseThrow(() -> new Exception("倉庫「" + wName + "」がマスタに存在しません。"));

	            // 3. 【重要】店舗と倉庫の連携チェック（他店の倉庫への誤発注を防止）
	            boolean isRelated = relationRepository.existsByShopAndWarehouseAndDeleteFlag(shop, warehouse, 0);
	            if (!isRelated) {
	                throw new Exception("倉庫「" + wName + "」とは連携設定がされていないため、発注できません。");
	            }

	            // 4. 数値変換とバリデーション
	            int amount;
	            try {
	                amount = Integer.parseInt(dto.getAmount().trim());
	                if (amount <= 0) throw new Exception("数量は1以上で入力してください。");
	            } catch (NumberFormatException e) {
	                throw new Exception("数量「" + dto.getAmount() + "」は半角数字で入力してください。");
	            }

	            // 5. 倉庫在庫チェック
	            WarehouseStock wStock = warehouseStockRepository.findByWarehouseIdAndGoodsIdAndDeleteFlag(warehouse, goods, 0);
	            int currentStock = (wStock == null) ? 0 : wStock.getWarehouseStockQuantity();
	            
	            if (currentStock < amount) {
	                throw new Exception("倉庫在庫が不足しています（現在の在庫: " + currentStock + "）。数量を調整してください。");
	            }

	            // 6. 倉庫在庫の減算（発注確定時に在庫を確保する場合）
	            wStock.setWarehouseStockQuantity(currentStock - amount);
	            warehouseStockRepository.save(wStock);

	            // 7. 発注レコードの作成・保存
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

	        } catch (Exception e) {
	            // シート名と行番号を付与してエラーリストに蓄積
	            errorMessages.add("【" + dto.getSheetName() + "】" + dto.getExcelRowNum() + "行目: " + e.getMessage());
	        }
	    }

	    // 全てのチェックが終わった後、1つでもエラーがあれば例外を投げて全ロールバック
	    if (!errorMessages.isEmpty()) {
	        throw new Exception(String.join("<br>", errorMessages));
	    }
	}
	
	
	public byte[] generateOrderExcel(Shop shop) throws Exception {
	    List<Category> categories = categoryRepository.findAll();
	    
	    // 連携倉庫名の取得
	    List<String> warehouseNames = relationRepository.findByShopAndDeleteFlag(shop, 0)
	            .stream()
	            .map(r -> r.getWarehouse().getWarehouseName())
	            .toList();

	    try (Workbook workbook = new XSSFWorkbook(); 
	         ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	        
	        String[] warehouseArray = warehouseNames.toArray(new String[0]);

	        for (Category category : categories) {
	            Sheet sheet = workbook.createSheet(category.getCategoryName());
	            DataValidationHelper helper = sheet.getDataValidationHelper();

	            // ヘッダー
	            Row header = sheet.createRow(0);
	            header.createCell(0).setCellValue("商品名");
	            header.createCell(1).setCellValue("宛先倉庫名");
	            header.createCell(2).setCellValue("発注数");

	            // --- 【要件反映】店舗在庫(ShopStock)にある、このカテゴリの商品名だけを抽出 ---
	            // ※ shopStockRepository に findByShopIdAndGoodsId_Category などのメソッドがある想定
	            // なければ GoodsRepository から「この店舗の在庫テーブルに存在する商品」を絞り込みます
	            List<String> goodsNames = shopStockRepository.findByShopIdAndDeleteFlag(shop, 0)
	                    .stream()
	                    .map(ShopStock::getGoodsId) // Goodsエンティティ取得
	                    .filter(g -> g.getCategory().equals(category)) // カテゴリ一致
	                    .map(Goods::getGoodsName) // 名前取得
	                    .distinct()
	                    .toList();

	            if (!goodsNames.isEmpty()) {
	                String[] goodsArray = goodsNames.toArray(new String[0]);
	                
	                // A列: 商品名、B列: 倉庫名
	                CellRangeAddressList goodsRange = new CellRangeAddressList(1, 100, 0, 0);
	                CellRangeAddressList warehouseRange = new CellRangeAddressList(1, 100, 1, 1);

	                // ドロップダウン設定
	                sheet.addValidationData(helper.createValidation(
	                        helper.createExplicitListConstraint(goodsArray), goodsRange));
	                
	                if (warehouseArray.length > 0) {
	                    sheet.addValidationData(helper.createValidation(
	                            helper.createExplicitListConstraint(warehouseArray), warehouseRange));
	                }
	            }

	            sheet.setColumnWidth(0, 8000);
	            sheet.setColumnWidth(1, 8000);
	        }

	        workbook.write(out);
	        return out.toByteArray();
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