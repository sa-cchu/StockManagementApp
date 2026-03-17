package com.github.sa_cchu.stock.util;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.github.sa_cchu.stock.dto.OrderHistoryDto;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

public class PdfExportUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void exportOrderHistoryPdf(HttpServletResponse response, String filename, List<OrderHistoryDto> orderList, boolean isShop) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        // A4ヨコでドキュメントを作成
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // 日本語フォント（IPAexゴシック等を入れた場合はそのパスを指定）
            // staticなresourcesに配置したフォントファイルを読み込む
            String fontPath = "src/main/resources/fonts/ipaexg.ttf";
            // NOTE: デプロイ時にはクラスパスのリソースとして読み込む必要がありますが、開発環境ではファイルパスで動作します
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 10, Font.NORMAL);
            Font headerFont = new Font(baseFont, 10, Font.BOLD);

            // テーブル作成: カラム数7
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // カラム幅比率の設定
            float[] columnWidths = {1f, 3f, 3f, 1.5f, 1.5f, 2.5f, 2.5f};
            table.setWidths(columnWidths);

            // ヘッダー追加
            String[] headers = {
                    "発注ID",
                    isShop ? "商品名" : "発注元店舗",
                    isShop ? "発注先倉庫" : "商品名",
                    "発注個数",
                    "ステータス",
                    "発注日時",
                    "更新日時"
            };

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                cell.setBackgroundColor(new java.awt.Color(230, 230, 230)); // 軽いグレー
                cell.setPadding(5f);
                table.addCell(cell);
            }

            // データ追加
            for (OrderHistoryDto order : orderList) {
                String targetName = isShop ? order.getWarehouseName() : order.getShopName();

                table.addCell(createCell(String.valueOf(order.getOrderId()), font, com.lowagie.text.Element.ALIGN_RIGHT));
                table.addCell(createCell(isShop ? order.getGoodsName() : targetName, font, com.lowagie.text.Element.ALIGN_LEFT));
                table.addCell(createCell(isShop ? targetName : order.getGoodsName(), font, com.lowagie.text.Element.ALIGN_LEFT));
                table.addCell(createCell(order.getOrderAmount() + "個", font, com.lowagie.text.Element.ALIGN_RIGHT));
                table.addCell(createCell(order.getOrderStatus(), font, com.lowagie.text.Element.ALIGN_CENTER));
                table.addCell(createCell(order.getOrderDate() != null ? order.getOrderDate().format(FORMATTER) : "", font, com.lowagie.text.Element.ALIGN_CENTER));
                table.addCell(createCell(order.getUpdateDate() != null ? order.getUpdateDate().format(FORMATTER) : "", font, com.lowagie.text.Element.ALIGN_CENTER));
            }

            document.add(table);

        } catch (DocumentException e) {
            throw new IOException("PDFの作成に失敗しました", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        return cell;
    }
}
