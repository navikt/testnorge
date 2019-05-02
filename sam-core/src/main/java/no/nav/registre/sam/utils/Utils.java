package no.nav.registre.sam.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Timestamp formatTimestamp(String dateString) throws ParseException {
        if ("".equals(dateString)) {
            return getTodaysDate();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date parsedDate = dateFormat.parse(dateString);
        return new java.sql.Timestamp(parsedDate.getTime());
    }

    public static java.sql.Date formatDate(String dateString) throws ParseException {
        if (dateString == null || "".equals(dateString)) {
            return new java.sql.Date(getTodaysDate().getTime());
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return new java.sql.Date(dateFormat.parse(dateString).getTime());
        }
    }

    public static Timestamp getTodaysDate() {
        return new Timestamp(System.currentTimeMillis());
    }
}
