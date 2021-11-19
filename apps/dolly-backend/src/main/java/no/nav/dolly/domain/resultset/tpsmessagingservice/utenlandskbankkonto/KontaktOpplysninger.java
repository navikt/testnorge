package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public final class KontaktOpplysninger {
    private final EndringKontonummer endringAvkontonr;


    @Data
    @Builder
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class EndringKontonummer {

        private final UtenlandskBankkonto endreKontonrUtland;
    }

    @Data
    @Builder
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class UtenlandskBankkonto {

        private final String giroNrUtland;
        private final String kodeSwift;
        private final String kodeLand;
        private final String bankNavn;
        private final String valuta;
        private final String bankAdresse1;
        private final String bankAdresse2;
        private final String bankAdresse3;
    }
}
