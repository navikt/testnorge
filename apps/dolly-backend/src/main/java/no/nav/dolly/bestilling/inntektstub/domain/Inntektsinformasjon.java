package no.nav.dolly.bestilling.inntektstub.domain;

import lombok.*;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Inntektsinformasjon {

    private String norskIdent;
    private String aarMaaned;
    private String opplysningspliktig;
    private String virksomhet;

    @EqualsAndHashCode.Exclude
    private List<Inntekt> inntektsliste;

    @EqualsAndHashCode.Exclude
    private List<Fradrag> fradragsliste;

    @EqualsAndHashCode.Exclude
    private List<Forskuddstrekk> forskuddstrekksliste;

    @EqualsAndHashCode.Exclude
    private List<Arbeidsforhold> arbeidsforholdsliste;

    private LocalDateTime rapporteringsdato;

    private Integer versjon;

    @EqualsAndHashCode.Exclude
    private String feilmelding;

    public static Flux<Inntektsinformasjon> of(WebClientError.Description description) {
        return Flux.just(Inntektsinformasjon
                .builder()
                .feilmelding(description.getMessage())
                .build());
    }

    public List<Inntekt> getInntektsliste() {
        if (isNull(inntektsliste)) {
            inntektsliste = new ArrayList<>();
        }
        return inntektsliste;
    }

    public List<Fradrag> getFradragsliste() {
        if (isNull(fradragsliste)) {
            fradragsliste = new ArrayList<>();
        }
        return fradragsliste;
    }

    public List<Forskuddstrekk> getForskuddstrekksliste() {
        if (isNull(forskuddstrekksliste)) {
            forskuddstrekksliste = new ArrayList<>();
        }
        return forskuddstrekksliste;
    }

    public List<Arbeidsforhold> getArbeidsforholdsliste() {
        if (isNull(arbeidsforholdsliste)) {
            arbeidsforholdsliste = new ArrayList<>();
        }
        return arbeidsforholdsliste;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fradrag {

        @EqualsAndHashCode.Exclude
        private Long id;

        private Double beloep;
        private String beskrivelse;

        @EqualsAndHashCode.Exclude
        private String feilmelding;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Forskuddstrekk {

        @EqualsAndHashCode.Exclude
        private Long id;

        private Double beloep;
        private String beskrivelse;

        @EqualsAndHashCode.Exclude
        private String feilmelding;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsforhold {

        @EqualsAndHashCode.Exclude
        private Long id;

        private Double antallTimerPerUkeSomEnFullStillingTilsvarer;
        private String arbeidsforholdstype;
        private String arbeidstidsordning;
        private String avloenningstype;

        private LocalDate sisteDatoForStillingsprosentendring;
        private LocalDate sisteLoennsendringsdato;
        private LocalDate sluttdato;

        private LocalDate startdato;
        private Double stillingsprosent;
        private String yrke;

        @EqualsAndHashCode.Exclude
        private String feilmelding;
    }
}
