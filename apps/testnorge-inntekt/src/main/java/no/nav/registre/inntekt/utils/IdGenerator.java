package no.nav.registre.inntekt.utils;

import java.util.Random;

public class IdGenerator {
    public static String randomNumberString(int length) {
        Random r = new Random();
        int min = (int)Math.pow(10, length-1);
        long n = min + r.nextInt(min*9);

        return String.valueOf(n);
    }
}
