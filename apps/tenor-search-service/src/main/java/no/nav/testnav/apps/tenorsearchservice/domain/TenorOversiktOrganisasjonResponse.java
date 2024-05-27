package no.nav.testnav.apps.tenorsearchservice.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorOversiktOrganisasjonResponse {

    private HttpStatus status;
    private Data data;
    private String query;
    private String error;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private Integer treff;
        private Integer rader;
        private Integer offset;
        private Integer nesteSide;
        private Integer seed;
        private List<Organisasjon> organisasjoner;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Organisasjon {

        private String navn;
        private String organisasjonsnummer;
        private Organisasjonsform organisasjonsform;
        private Adresse forretningsadresse;
        private Adresse postadresse;
        private List<String> kilder;
        private List<Naeringskode> naeringskoder;
        private String registreringsdatoEnhetsregisteret;
        private String slettetIEnhetsregisteret;
        private String registrertIForetaksregisteret;
        private String slettetIForetaksregisteret;
        private String registreringspliktigForetaksregisteret;
        private String registrertIFrivillighetsregisteret;
        private String registrertIStiftelsesregisteret;
        private String registrertIMvaregisteret;
        private String konkurs;
        private String underAvvikling;
        private String underTvangsavviklingEllerTvangsopplosning;
        private String maalform;
        private String ansvarsbegrensning;
        private String harAnsatte;
        private Integer antallAnsatte;
        private Underenhet underenhet;
        private String bedriftsforsamling;
        private String representantskap;
        private List<Object> enhetstatuser;
        private List<Object> fullmakter;
        private Kapital kapital;
        private String kjonnsrepresentasjon;
        private List<MatrikkelEnhet> matrikkelnummer;
        private FravalgAvRevisjon fravalgAvRevisjon;
        private NorskregistrertUtenlandskForetak norskregistrertUtenlandskForetak;
        private LovgivningOgForetaksformIHjemlandet lovgivningOgForetaksformIHjemlandet;
        private RegisterIHjemlandet registerIHjemlandet;
        private JsonNode kildedata;
        private JsonNode tenorRelasjoner;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Organisasjonsform {

        private String kode;
        private String beskrivelse;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adresse {
        private String land;
        private String landkode;
        private String postnummer;
        private String poststed;
        private List<String> adresse;
        private String kommune;
        private String kommunenummer;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatrikkelEnhet {
        private String matrikkelEnhetId;
        private String kommunenummer;
        private Integer gaardsnummer;
        private Integer bruksnummer;
        private Integer festenummer;
        private Integer rekkefolge;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Naeringskode {
        private String kode;
        private String beskrivelse;
        private boolean hjelpeenhetskode;
        private int rekkefolge;
        private int nivaa;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Underenhet {
        private String hovedenhet;
        private String oppstartsdato;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kapital {
        private String antallAksjer;
        private List<String> fritekst;
        private String sakkyndigRedegjorelse;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FravalgAvRevisjon {
        private String fravalg;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NorskregistrertUtenlandskForetak {
        private String helNorskEierskap;
        private String aktivitetINorge;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LovgivningOgForetaksformIHjemlandet {
        private String foretaksform;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterIHjemlandet {
        private List<String> navnRegister;
        private List<String> adresse;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Type {
        private String kode;
        private String beskrivelse;
    }

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Virksomhet {
        private String organisasjonsnummer;
    }
}