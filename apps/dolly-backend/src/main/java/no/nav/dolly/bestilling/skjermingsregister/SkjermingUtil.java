package no.nav.dolly.bestilling.skjermingsregister;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@UtilityClass
public class SkjermingUtil {

    public static boolean isSkjerming(RsDollyUtvidetBestilling bestilling) {

        return nonNull(bestilling.getSkjerming()) &&
                (nonNull(bestilling.getSkjerming().getEgenAnsattDatoFom()) ||
                        nonNull(bestilling.getSkjerming().getEgenAnsattDatoTom()));
    }

    public static boolean isTpsMessagingEgenansatt(RsDollyUtvidetBestilling bestilling) {

        return nonNull(bestilling.getTpsMessaging()) &&
                (nonNull(bestilling.getTpsMessaging().getEgenAnsattDatoFom()) ||
                        nonNull(bestilling.getTpsMessaging().getEgenAnsattDatoTom()));
    }

    public static LocalDateTime getEgenansattDatoFom(RsDollyUtvidetBestilling bestilling) {

        if (isSkjerming(bestilling)) {
            return bestilling.getSkjerming().getEgenAnsattDatoFom();

        } else if (isTpsMessagingEgenansatt(bestilling)) {
            return toLocalDateTime(bestilling.getTpsMessaging().getEgenAnsattDatoFom());

        } else {
            return null;
        }
    }

    public static LocalDateTime getEgenansattDatoTom(RsDollyUtvidetBestilling bestilling) {

        if (isSkjerming(bestilling)) {
            return bestilling.getSkjerming().getEgenAnsattDatoTom();

        } else if (isTpsMessagingEgenansatt(bestilling)) {
            return toLocalDateTime(bestilling.getTpsMessaging().getEgenAnsattDatoTom());

        } else {
            return null;
        }
    }

    private static LocalDateTime toLocalDateTime(LocalDate dato) {

        return nonNull(dato) ? dato.atStartOfDay() : null;
    }
}
