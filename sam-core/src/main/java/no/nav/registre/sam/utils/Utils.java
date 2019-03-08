package no.nav.registre.sam.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Timestamp formatDate(String dateString){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date parsedDate = dateFormat.parse(dateString);
            return new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            System.out.println(e.toString());
            return null;
        }
    }

    public static Timestamp getTodaysDate(){
        Long date = new Date().getTime();
        Timestamp t = new Timestamp(date);
        System.out.println(t);
        return t;
    }
}
