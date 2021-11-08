package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KontaktopplysningerRequestDTO extends TpsServiceRoutineEndringDTO {

    private Spraak endringAvSprak;
    private Kontonummer endringAvKontonr;
    private NavAdresse endringAvNAVadresse;
    private Telefon endringAvTelefon;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NavAdresse {

        private TiadAdresse nyAdresseNavNorge;
        private UtadAdresse nyAdresseNavUtland;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TiadAdresse {

        private String datoTom; //yyyy-mm-dd
        private String typeAdresseNavNorge;
        private String typeTilleggsLinje;
        private String tilleggsLinje;
        private String kommunenr;
        private String gatekode;
        private String gatenavn;
        private String husnr;
        private String husbokstav;
        private String eiendomsnavn;
        private String bolignr;
        private String postboksnr;
        private String postboksAnlegg;
        private String postnr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class UtadAdresse {

        private String datoTom; //yyyy-mm-dd
        private String adresse1;
        private String adresse2;
        private String adresse3;
        private String kodeLand;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Telefon {

        @JacksonXmlElementWrapper(useWrapping = false)
        private List<NyTelefon> nyTelefon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NyTelefon {

        private String typeTelefon;
        private String telefonLandkode;
        private String telefonNr;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Kontonummer {

        private NorskKontonummer endreKontonrNorsk;
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
    public static class Spraak {

        private NyttSprak endreSprak;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NyttSprak {
        private String sprakKode;
        private String datoSprak; //yyyy-mm-dd
    }
}
