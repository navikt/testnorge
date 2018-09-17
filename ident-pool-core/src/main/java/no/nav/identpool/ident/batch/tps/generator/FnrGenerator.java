package no.nav.identpool.ident.batch.tps.generator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

@Service
public class FnrGenerator {

    private static final int[] CONTROL_DIGIT_C1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] CONTROL_DIGIT_C2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");

    public String[] genererIdenterArray(LocalDate fom, LocalDate tom) {
        return genererIdenter(fom, tom).toArray(new String[0]);
    }

    private HashSet<String> genererIdenter(LocalDate fom, LocalDate tom) {
        if (fom.isAfter(tom)) {
            throw new IllegalArgumentException(String.format("Dato fra og med %s, må være før eller lik dato til og med %s", fom.toString(), tom.toString()));
        }
        tom = tom.plusDays(1);
        HashSet<String> identSet = new HashSet<>();
        while(!fom.equals(tom)) {
            generateNumbers(identSet, fom);
            fom = fom.plusDays(1);
        }
        return identSet;
    }

    private void generateNumbers(HashSet<String> set, LocalDate birthdate) {
        String date = formatter.format(birthdate) + "%03d";
        getCategoryRange(birthdate)
                .mapToObj(number -> this.generateFnr(String.format(date, number)))
                .filter(Objects::nonNull)
                .forEach(set::add);
    }

    private IntStream getCategoryRange(LocalDate birthDate) {
        int year = birthDate.getYear();
        if (year < 2000 && year > 1) {
            return IntStream.range(1, 500);
        } else if (year >= 2000 && year < 2040) {
            return IntStream.range(500, 1000);
        } else {
            throw new IllegalStateException(String.format("Fødelsår må være mellom 1 og 2039, fikk %d", year));
        }
    }

    private String generateFnr(String number) {
        int digit1 = getControlDigit(number, CONTROL_DIGIT_C1);
        if (digit1 == 10) {
            return null;
        }
        number += digit1;
        int digit2 = getControlDigit(number, CONTROL_DIGIT_C2);
        if (digit2 == 10) {
            return null;
        }
        return number + digit2;
    }

    private int getControlDigit(String fnr, int... sequence) {
        int digitsum = 0;
        for(int i = 0; i < sequence.length; ++i) {
            digitsum += (fnr.charAt(i) - 48) * sequence[i];
        }
        digitsum = 11 - (digitsum % 11);
        return digitsum == 11 ? 0 : digitsum;
    }
}
