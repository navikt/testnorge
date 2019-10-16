package no.nav.dolly.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListUtil {

    private ListUtil() {
    }

    public static <T> List<T> listOf(T... t) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, t);
        return list;
    }
}
