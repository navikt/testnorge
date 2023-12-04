package no.nav.dolly.bestilling.arenaforvalter;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@UtilityClass
@Slf4j
public class ArenaUtils {

    public static final String OPPRETTET = "Oppretting: ";
    public static final String AVSLAG = "Avslag: ";
    public static final String INAKTIVERT = "Inaktivert: ";
    public static final String STATUS_FMT = "%s:%s";
    public static final String IDENT = "ident";
    public static final String MILJOE = "miljoe";
    public static final String STANSET = "Stanset forrige vedtak: ";

    public static LocalDate toLocalDate(LocalDateTime localDateTime) {

        return nonNull(localDateTime) ? localDateTime.toLocalDate() : null;
    }

    public static String fixFormatUserDefinedError(String decoded) {
        try {
            if (!decoded.contains("UserDefinedResourceError")) {
                return decoded;
            }

            var feilmeldingStart = decoded.indexOf("Error Message");
            var feilmeldingSlutt = decoded.indexOf("\\n", feilmeldingStart) - 1;
            var feilmelding = feilmeldingStart != -1 && feilmeldingSlutt != -1 ? "Feil:" + decoded.substring(feilmeldingStart, feilmeldingSlutt + 1) : decoded;
            var feilmeldingUtenUrelevantInfo = feilmelding.replace("=", ":")
                    .replace("&2", "")
                    .replaceAll("/", "")
                    .replace("\\", "")
                    .replaceAll("request for.*", "request"); //Fjerner URL og påfølgende feilmelding som er urelevant for bruker
            var feilmeldingInformasjonStart = feilmeldingUtenUrelevantInfo.lastIndexOf(":") + 2;
            return feilmelding.equals(decoded) ? decoded : "Feil: " + feilmeldingUtenUrelevantInfo.substring(feilmeldingInformasjonStart).stripTrailing();
        } catch (NullPointerException exception) {
            log.error("Klarte ikke å parse feilmelding fra ArenaException", exception);
            return decoded;
        }
    }
}
