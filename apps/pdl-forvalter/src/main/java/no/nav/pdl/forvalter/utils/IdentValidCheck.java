package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class IdentValidCheck {

    private static final int[] CHECK_DIGIT_C1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] CHECK_DIGIT_C2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    public Set<String> isIdentValid(Collection<String> identer) {

        return identer.stream()
                .filter(ident -> ident.length() == 11 &&
                        isValidFnrDnrOrBost(ident) &&
                        isCheckDigitValid(ident))
                .collect(Collectors.toSet());
    }

    private boolean isValidFnrDnrOrBost(String ident) {
        var day = Integer.parseInt(ident.substring(0, 2));
        var month = Integer.parseInt(ident.substring(2, 4));
        return isFnr(day, month) || isDnr(day, month) || isBost(day, month);
    }

    private boolean isBost(int day, int month) {
        return day > 0 && day < 32 &&
                (month > 20 && month < 33 || month > 60 && month < 73);
    }

    private boolean isDnr(int day, int month) {
        return day > 40 && day < 72 &&
                (month > 0 && month < 13 || month > 40 && month < 53);
    }

    private boolean isFnr(int day, int month) {
        return day > 0 && day < 32 &&
                (month > 0 && month < 13 || month > 40 && month < 53);
    }

    private boolean isCheckDigitValid(String ident) {
        var checkDigit1 = calculateCheckDigit(ident, true, CHECK_DIGIT_C1);
        var checkDigit2 = calculateCheckDigit(ident, false, CHECK_DIGIT_C2);

        var calculatedDigits = String.format("%d%d", checkDigit1, checkDigit2);
        var actualDigits = ident.substring(9, 11);
        return calculatedDigits.equals(actualDigits);
    }

    private int calculateCheckDigit(String ident, boolean isFirstCheckDigit, int... multipliers) {
        var xontrolDigit = 0;
        var skipEnd = isFirstCheckDigit ? 2 : 1;
        for (int index = 0; index < ident.length() - skipEnd; index++) {
            int number = Character.getNumericValue(ident.charAt(index));
            xontrolDigit += number * multipliers[index];
        }
        xontrolDigit = Math.abs((xontrolDigit % 11) - 11);

        return xontrolDigit == 11 ? 0 : xontrolDigit;
    }
}
