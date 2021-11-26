package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlType;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KontaktopplysningerRequestDTO {

    private Spraak endringAvSprak;
    private Kontonummer endringAvKontonr;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Kontonummer {

        private NorskKontonummer endreKontonrNorsk;
        private UtenlandskKontonummer endreKontonrUtland;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NorskKontonummer {

        private String giroNrNorsk;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class UtenlandskKontonummer {

        private String giroNrUtland;
        private String kodeSwift;
        private String kodeLand;
        private String bankNavn;
        private String valuta;
        private String bankAdresse1;
        private String bankAdresse2;
        private String bankAdresse3;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Spraak {

        private NyttSprak endreSprak;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"sprakKode", "datoSprak"})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NyttSprak {
        private String sprakKode;
        private LocalDate datoSprak;
    }
}
