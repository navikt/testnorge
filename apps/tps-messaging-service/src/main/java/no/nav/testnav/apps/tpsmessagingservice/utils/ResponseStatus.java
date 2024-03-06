package no.nav.testnav.apps.tpsmessagingservice.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@UtilityClass
public final class ResponseStatus {

    public static String extract(String status) {

        if (nonNull(status) && status.contains("FEIL")) {
            return status;
        } else if (nonNull(status) && status.length() > 3) {
            return format("FEIL: %s", status.substring(3).replaceAll("\\d*%[A-Z]\\d*%", "").replaceAll("%; *", ""));
        } else {
            return "STATUS: TIDSAVBRUDD";
        }
    }

    public static TpsMeldingResponse decodeStatus(TpsMeldingResponse response) {

        return nonNull(response) ?
                TpsMeldingResponse.builder()
                        .returStatus("00".equals(response.getReturStatus()) || "04".equals(response.getReturStatus()) ? "OK" : "FEIL")
                        .returMelding(response.getReturMelding())
                        .utfyllendeMelding(response.getUtfyllendeMelding())
                        .build() :
                TpsMeldingResponse.builder()
                        .returStatus("FEIL")
                        .returMelding("Ingen data")
                        .utfyllendeMelding("Melding fra TPS er tom")
                        .build();
    }
}