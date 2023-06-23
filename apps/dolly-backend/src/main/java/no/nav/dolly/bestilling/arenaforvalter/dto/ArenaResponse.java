package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ArenaResponse {

    private String miljoe;
    private HttpStatus status;
    private String feilmelding;

    private String fodselsnr;
    private LocalDate registrertDato;
    private LocalDate sistInaktivDato;
    private String maalform;
    private String statsborgerLand;
    private String bosattStatus;
    private NavKontor lokalkontor;
    private String hovedmaal;
    private Egenskap formidlingsgruppe;
    private Egenskap servicegruppe;
    private Egenskap rettighetsgruppe;
    private Boolean meldeplikt;
    private String meldeform;
    private String meldegruppe;
    private List<Vedtak> vedtakListe;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NavKontor {

        String enhetNr;
        String enhetNavn;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Egenskap {

        private String kode;
        private String navn;
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper=true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sak extends Egenskap {

        private String status;
        private String sakNr;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Vedtak {

        private Sak sak;
        private Integer vedtakNr;
        private Egenskap rettighet;
        private Egenskap aktivitetfase;
        private Egenskap type;
        private Egenskap status;
        private String utfall;
        private LocalDate fraDato;
        private LocalDate tilDato;
    }
}
