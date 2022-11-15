package no.nav.dolly.bestilling.inntektstub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
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

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class BilOgBaat {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BonusFraForsvaret {

        private String aaretUtbetalingenGjelderFor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    public static class DagmammaIEgenBolig {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periode {

        private LocalDate startdato;
        private LocalDate sluttdato;
    }

    @Data
    @Builder
    @NoArgsConstructor
    public static class NorskKontinentalsokkel {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inntjeningsforhold {

        private String inntjeningsforhold;
    }

    @Data
    @Builder
    @NoArgsConstructor
    public static class Livrente {}

    @Data
    @Builder
    @NoArgsConstructor
    public static class LottOgPartInnenFiske {}

    @Data
    @Builder
    @NoArgsConstructor
    public static class Nettoloennsordning {}

    @Data
    @Builder
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReiseKostOgLosji {

        private String persontype;
    }

    @Data
    @Builder
    @NoArgsConstructor
    public static class UtenlandskArtist {}
}
