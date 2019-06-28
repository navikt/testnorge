package no.nav.dolly.aareg;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.feil.ForretningsmessigUnntak;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
public final class AaregResponseHandler {

    private AaregResponseHandler() {

    }

    public static String extractError(Exception exception) {

        String feilbeskrivelse = "";
        try {
            Method method = exception.getClass().getMethod("getFaultInfo");
            ForretningsmessigUnntak faultInfo = (ForretningsmessigUnntak) method.invoke(exception);
            if (nonNull(faultInfo.getTidspunkt())) {
                XMLGregorianCalendar tidspunkt = faultInfo.getTidspunkt();
                feilbeskrivelse = format(" (ForretningsmessigUnntak: feilaarsak: %s, feilkilde: %s, feilmelding: %s, tidspunkt: %s)",
                        faultInfo.getFeilaarsak(), faultInfo.getFeilkilde(), faultInfo.getFeilmelding(),
                        LocalDateTime.of(tidspunkt.getYear(), tidspunkt.getMonth(), tidspunkt.getDay(), tidspunkt.getHour(), tidspunkt.getMinute()));
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Lesing av faultInfo fra Aaareg feilet.", e);
        }
        return format("Feil, %s -> %s%s", exception.getClass().getSimpleName(), exception.getMessage(), feilbeskrivelse);
    }
}
