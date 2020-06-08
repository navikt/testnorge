package no.nav.registre.sam.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtils {

    private DateUtils() {
    }

    public static Timestamp formatTimestamp(String dateString) throws ParseException {
        if (dateString == null) {
            return null;
        } else if ("".equals(dateString)) {
            return getTodaysDate();
        } else {
            var dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return new Timestamp(dateFormat.parse(dateString).getTime());
        }
    }

    public static Date formatDate(String dateString) throws ParseException {
        if (dateString == null) {
            return null;
        } else if ("".equals(dateString)) {
            return new Date(getTodaysDate().getTime());
        } else {
            var dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return new Date(dateFormat.parse(dateString).getTime());
        }
    }

    public static Timestamp getTodaysDate() {
        return new Timestamp(System.currentTimeMillis());
    }
}
