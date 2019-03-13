package no.nav.dolly.aareg;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.feil.ForretningsmessigUnntak;

@Slf4j
public final class AaregResponseHandler {

    private AaregResponseHandler() {

    }

    public static String extractError(Exception exception) {

        String feilbeskrivelse = "";
        try {
            Method method = exception.getClass().getMethod("getFaultInfo");
            ForretningsmessigUnntak faultInfo = (ForretningsmessigUnntak) method.invoke(exception);
            if (nonNull(faultInfo)) {
                feilbeskrivelse = format(" (ForretningsmessigUnntak: feilaarsak: %s, feilkilde: %s, feilmelding: %s%s)",
                        faultInfo.getFeilaarsak(), faultInfo.getFeilkilde(), faultInfo.getFeilmelding(),
                        (nonNull(faultInfo.getTidspunkt()) ? format(", tidspunkt: %s", faultInfo.getTidspunkt().toString()) : ""));
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Lesing af faultInfo fra Aaareg feilet.", e);
        }
        return format("Feil, %s -> %s%s", exception.getClass().getSimpleName(), exception.getMessage(), feilbeskrivelse);
    }
}
