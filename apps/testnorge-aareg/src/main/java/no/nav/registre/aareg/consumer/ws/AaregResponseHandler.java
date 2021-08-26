package no.nav.registre.aareg.consumer.ws;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.feil.ForretningsmessigUnntak;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@Slf4j final class AaregResponseHandler {

    private AaregResponseHandler() {
    }

    public static String extractError(Exception exception) {
        var feilbeskrivelse = "";
        try {
            var method = exception.getClass().getMethod("getFaultInfo");
            var faultInfo = (ForretningsmessigUnntak) method.invoke(exception);
            if (nonNull(faultInfo.getTidspunkt())) {
                var tidspunkt = faultInfo.getTidspunkt();
                feilbeskrivelse = format(" (ForretningsmessigUnntak: feilårsak: %s, feilkilde: %s, feilmelding: %s, tidspunkt: %s)",
                        faultInfo.getFeilaarsak(), faultInfo.getFeilkilde(), faultInfo.getFeilmelding(),
                        LocalDateTime.of(tidspunkt.getYear(), tidspunkt.getMonth(), tidspunkt.getDay(), tidspunkt.getHour(), tidspunkt.getMinute()));
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Lesing av faultInfo fra Aaareg feilet.", e);
        }
        return isBlank(feilbeskrivelse) ? "Feil: " + exception.getMessage() :
                format("Feil, %s -> %s%s", exception.getClass().getSimpleName(), exception.getMessage(), feilbeskrivelse);
    }
}
