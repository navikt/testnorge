package no.nav.dolly.domain.resultset.inntektsstub;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsInntektsinformasjon {

    private LocalDateTime aarMaaned;
    private String opplysningspliktig;
    private String virksomhet;

    private List<Inntekt> inntektsliste;
    private List<Fradrag> fradragsliste;
    private List<Forskuddstrekk> forskuddstrekksliste;
    private List<Arbeidsforhold> arbeidsforholdsliste;

    private LocalDateTime Innleveringstidspunkt;
    private String feilmelding;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fradrag {

        private Integer id;
        private Double beloep;
        private String beskrivelse;
        private String feilmelding;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Forskuddstrekk {

        private Integer id;
        private Double beloep;
        private String beskrivelse;
        private String feilmelding;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Arbeidsforhold {

        private Integer id;
        private Double antallTimerPerUkeSomEnFullStillingTilsvarer;
        private String arbeidsforholdstype;
        private String arbeidstidsordning;
        private String avloenningstype;
        private String feilmelding;

        private LocalDateTime sisteDatoForStillingsprosentendring;
        private LocalDateTime sisteLoennsendringsdato;
        private LocalDateTime sluttdato;
        private LocalDateTime startdato;

        private Double stillingsprosent;
        private String yrke;
    }
}
