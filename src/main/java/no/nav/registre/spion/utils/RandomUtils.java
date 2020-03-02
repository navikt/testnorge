package no.nav.registre.spion.utils;

import java.util.Random;

public class RandomUtils {

    private static final Random RAND = new Random();

    public static int getRandomBoundedNumber(int min , int max){
        return min + RAND.nextInt(max-min + 1);
    }

}
