package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
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

    public static Mono<ArenaStatusResponse> of(WebClientError.Description description, String miljoe) {
        return Mono.just(ArenaStatusResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .miljoe(miljoe)
                .build());
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
