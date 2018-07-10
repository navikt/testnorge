package no.nav.util;

import java.util.Collection;

public class UtilFunctions {

    public static boolean isNullOrEmpty(Object obj){
        return obj == null;
    }

    public static boolean isNullOrEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(String str){
        return str == null || str.isEmpty();
    }
}
