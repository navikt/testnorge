package no.nav.registre.sam.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Timestamp formatDate(String dateString) throws ParseException {
        if ("".equals(dateString)) {
            return getTodaysDate();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date parsedDate = dateFormat.parse(dateString);
        return new java.sql.Timestamp(parsedDate.getTime());
    }

    public static Timestamp getTodaysDate() {
        long date = new Date().getTime();
        return new Timestamp(date);
    }
}
