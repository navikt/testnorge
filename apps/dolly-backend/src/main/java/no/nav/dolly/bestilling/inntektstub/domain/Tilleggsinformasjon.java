package no.nav.dolly.bestilling.inntektstub.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Tilleggsinformasjon {

    private BilOgBaat bilOgBaat;
    private BonusFraForsvaret bonusFraForsvaret;
    private DagmammaIEgenBolig dagmammaIEgenBolig;
    private Periode etterbetalingsperiode;
    private NorskKontinentalsokkel inntektPaaNorskKontinentalsokkel;
    private Inntjeningsforhold inntjeningsforhold;
    private Livrente livrente;
    private LottOgPartInnenFiske lottOgPart;
    private Nettoloennsordning nettoloenn;
    private AldersUfoereEtterlatteAvtalefestetOgKrigspensjon pensjon;
    private ReiseKostOgLosji reiseKostOgLosji;
    private UtenlandskArtist utenlandskArtist;

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class BilOgBaat {}

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BonusFraForsvaret {

        private String aaretUtbetalingenGjelderFor;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class DagmammaIEgenBolig {}

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periode {

        private LocalDate startdato;
        private LocalDate sluttdato;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class NorskKontinentalsokkel {}

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inntjeningsforhold {

        private String inntjeningsforhold;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class Livrente {}

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class LottOgPartInnenFiske {}

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class Nettoloennsordning {}

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AldersUfoereEtterlatteAvtalefestetOgKrigspensjon {

        private Double grunnpensjonsbeloep;
        private Double heravEtterlattepensjon;
        private Integer pensjonsgrad;
        private Periode tidsrom;
        private Double tilleggspensjonsbeloep;
        private Integer ufoeregrad;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReiseKostOgLosji {

        private String persontype;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class UtenlandskArtist {}
}
