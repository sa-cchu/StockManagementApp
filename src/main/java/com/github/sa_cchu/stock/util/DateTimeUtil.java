package com.github.sa_cchu.stock.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeUtil {

    /**
     * 文字列の年月日（yyyy-MM-dd）を開始日時（00:00:00）に変換します。
     */
    public static LocalDateTime parseStartDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr).atStartOfDay();
    }

    /**
     * 文字列の年月日（yyyy-MM-dd）を終了日時（23:59:59.999999999）に変換します。
     */
    public static LocalDateTime parseEndDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr).atTime(LocalTime.MAX);
    }
}
