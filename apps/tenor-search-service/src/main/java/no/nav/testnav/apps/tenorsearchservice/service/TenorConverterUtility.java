package no.nav.testnav.apps.tenorsearchservice.service;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;

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

    private static String convertNotOperator(Boolean verdi) {

        return isFalse(verdi) ? NOT_OPERATOR : "";
    }

    public static String convertEnumWildcard(Enum<?> type) {

        return isNull(type) ? "" : " and %s:*".formatted(type.name());
    }

    public static String convertObject(String navn, Object verdi) {

        return isNull(verdi) || verdi instanceof String string && isBlank(string) ? "" : " and %s:%s".formatted(navn, verdi);
    }

    public static String convertIntervall(String intervallNavn, TenorRequest.Intervall intervall) {

        return isNull(intervall) ? "" : " and %s:[%s to %s]"
                .formatted(intervallNavn,
                        convertObject(intervall.getFraOgMed()),
                        convertObject(intervall.getTilOgMed()));
    }

    public static String convertPeriode(TenorRequest.MonthInterval intervall) {

        return isNull(intervall) ? "" : " and periode:[%s to %s]"
                .formatted(
                        convertObject(intervall.getFraOgMed()),
                        convertObject(intervall.getTilOgMed()));
    }

    public static String convertEnum(String enumNavn, Enum<?> enumVerdi) {

        return isNull(enumVerdi) ? "" : " and %s:%s".formatted(enumNavn, enumVerdi.name());
    }

    public static String convertDatoer(String datoNavn, TenorRequest.DatoIntervall datoIntervall) {

        return isNull(datoIntervall) ? "" :
                " and %s:[%s to %s]".formatted(datoNavn,
                        convertObject(datoIntervall.getFraOgMed()),
                        convertObject(datoIntervall.getTilOgMed()));
    }

    public static String convertBooleanWildcard(String booleanNavn, Boolean booleanVerdi) {

        return isNotTrue(booleanVerdi) ? "" : " and %s:*".formatted(booleanNavn);
    }

    private static String convertObject(Object object) {

        return isNull(object) ? "*" : object.toString();
    }
}
