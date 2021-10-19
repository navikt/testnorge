package no.nav.registre.sam.testutils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtils {

    public static Timestamp formatTimestamp(String dateString) throws ParseException {
        if (dateString == null) {
            return null;
        } else if ("".equals(dateString)) {
            return new Timestamp(System.currentTimeMillis());
        } else {
            var dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return new Timestamp(dateFormat.parse(dateString).getTime());
        }
    }

    public static Date formatDate(String dateString) throws ParseException {
        if (dateString == null) {
            return null;
        } else if ("".equals(dateString)) {
            return new Date(new Timestamp(System.currentTimeMillis()).getTime());
        } else {
            var dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return new Date(dateFormat.parse(dateString).getTime());
        }
    }
}
