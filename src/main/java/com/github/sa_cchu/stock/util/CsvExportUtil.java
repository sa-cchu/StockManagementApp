package com.github.sa_cchu.stock.util;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.github.sa_cchu.stock.dto.OrderHistoryDto;
import com.opencsv.CSVWriter;

import jakarta.servlet.http.HttpServletResponse;

public class CsvExportUtil {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public static void exportOrderHistoryCsv(HttpServletResponse response, String filename, List<OrderHistoryDto> list, boolean isShop) throws Exception {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            
            if (isShop) {
                writer.writeNext(new String[]{"発注ID", "商品名", "発注先倉庫", "発注個数", "ステータス", "発注日時", "更新日時"});
            } else {
                writer.writeNext(new String[]{"発注ID", "発注元店舗", "商品名", "発注個数", "ステータス", "発注日時", "更新日時"});
            }

            for (OrderHistoryDto order : list) {
                String targetName = isShop ? order.getWarehouseName() : order.getShopName();
                String[] row = {
                    String.valueOf(order.getOrderId()),
                    isShop ? order.getGoodsName() : targetName,
                    isShop ? targetName : order.getGoodsName(),
                    order.getOrderAmount() + "個",
                    order.getOrderStatus(),
                    order.getOrderDate().format(DTF),
                    order.getUpdateDate().format(DTF)
                };
                writer.writeNext(row);
            }
        }
    }
}
