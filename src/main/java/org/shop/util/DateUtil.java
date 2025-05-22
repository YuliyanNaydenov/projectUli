package org.shop.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private DateUtil() {}
        public static String format(LocalDate date) {
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
