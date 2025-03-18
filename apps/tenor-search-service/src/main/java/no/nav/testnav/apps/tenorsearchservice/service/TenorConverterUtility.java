package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
public class TenorConverterUtility {

    private static final String NOT_OPERATOR = " not ";

    public static String convertBooleanSpecial(String navn, Boolean verdi) {

        return isNull(verdi) ? "" : " and %s%s:*".formatted(convertNotOperator(verdi), navn);
    }

    public static String convertObject(String navn, Object verdi) {

        return isNull(verdi) || verdi instanceof String string && isBlank(string) ? "" : " and %s:%s".formatted(navn, verdi);
    }

    public static String convertStringList(String navn, List<String> verdi) {

        return isNull(verdi) || verdi.isEmpty() ? "" :
                verdi.stream()
                        .map(element -> " and %s:%s".formatted(navn, element))
                        .collect(Collectors.joining(""));
    }

    public static String convertString(String navn, String verdi) {

        return isBlank(verdi) ? "" : " and %s:\"%s\"".formatted(navn, verdi);
    }

    public static String convertIntervall(String intervallNavn, TenorRequest.Intervall intervall) {

        return isNull(intervall) ? "" : " and %s:[%s to %s]"
                .formatted(intervallNavn,
                        convertObjectWildCard(intervall.getFraOgMed()),
                        convertObjectWildCard(intervall.getTilOgMed()));
    }

    public static String convertPeriode(TenorRequest.MonthInterval intervall) {

        return isNull(intervall) ? "" : " and periode:[%s to %s]"
                .formatted(
                        convertObjectWildCard(intervall.getFraOgMed()),
                        convertObjectWildCard(intervall.getTilOgMed()));
    }

    public static String convertEnum(String enumNavn, Enum<?> enumVerdi) {

        return isNull(enumVerdi) ? "" : " and %s:%s%s".formatted(enumNavn,
                enumVerdi.name().substring(0, 1).toLowerCase(),
                enumVerdi.name().substring(1));
    }

    public static String convertDatoer(String datoNavn, TenorRequest.DatoIntervall datoIntervall) {

        return isNull(datoIntervall) ? "" :
                " and %s:[%s to %s]".formatted(datoNavn,
                        convertObjectWildCard(datoIntervall.getFraOgMed()),
                        convertObjectWildCard(datoIntervall.getTilOgMed()));
    }

    public static String convertBooleanWildcard(String booleanNavn, Boolean booleanVerdi) {

        return isNotTrue(booleanVerdi) ? "" : " and %s:*".formatted(booleanNavn);
    }

    public static String convertObjectWildCard(Object object) {

        return isNull(object) ? "*" : object.toString();
    }

    public static String guard(StringBuilder builder) {

        return builder.substring(builder.isEmpty() ? 0 : 5, builder.length());
    }

    private static String convertNotOperator(Boolean verdi) {

        return isFalse(verdi) ? NOT_OPERATOR : "";
    }
}