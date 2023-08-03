package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class ArenaStatusResponse extends ArenaResponse {

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

    @Builder
    public ArenaStatusResponse(HttpStatus status, String miljoe, String feilmelding, String fodselsnr, LocalDate registrertDato, LocalDate sistInaktivDato, String maalform, String statsborgerLand, String bosattStatus, NavKontor lokalkontor, String hovedmaal, Egenskap formidlingsgruppe, Egenskap servicegruppe, Egenskap rettighetsgruppe, Boolean meldeplikt, String meldeform, String meldegruppe, List<Vedtak> vedtakListe) {
        super(status, miljoe, feilmelding);
        this.fodselsnr = fodselsnr;
        this.registrertDato = registrertDato;
        this.sistInaktivDato = sistInaktivDato;
        this.maalform = maalform;
        this.statsborgerLand = statsborgerLand;
        this.bosattStatus = bosattStatus;
        this.lokalkontor = lokalkontor;
        this.hovedmaal = hovedmaal;
        this.formidlingsgruppe = formidlingsgruppe;
        this.servicegruppe = servicegruppe;
        this.rettighetsgruppe = rettighetsgruppe;
        this.meldeplikt = meldeplikt;
        this.meldeform = meldeform;
        this.meldegruppe = meldegruppe;
        this.vedtakListe = vedtakListe;
    }

    public List<Vedtak> getVedtakListe() {

        if (isNull(vedtakListe)) {
            vedtakListe = new ArrayList<>();
        }
        return vedtakListe;
    }

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
    @EqualsAndHashCode(callSuper = true)
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

        public boolean isVedtak() {

            return "Ja".equals(utfall) &&
                    ("AAP".equals(rettighet.getKode()) ||
                    "DAGO".equals(rettighet.getKode()));
        }
    }
}
