package no.nav.dolly.domain.resultset.inntektsstub;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
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
    @NoArgsConstructor
    private static class BilOgBaat {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BonusFraForsvaret {

        private String aaretUtbetalingenGjelderFor;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    private static class DagmammaIEgenBolig {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Periode {

        @ApiModelProperty(
                dataType = "LocalDateTime",
                example = "yyyy-MM-dd",
                position = 1
        )
        private LocalDateTime startdato;

        @ApiModelProperty(
                dataType = "LocalDateTime",
                example = "yyyy-MM-dd",
                position = 2
        )
        private LocalDateTime sluttdato;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    private static class NorskKontinentalsokkel {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Inntjeningsforhold {

        private String inntjeningsforhold;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    private static class Livrente {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    private static class LottOgPartInnenFiske {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    private static class Nettoloennsordning {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AldersUfoereEtterlatteAvtalefestetOgKrigspensjon {

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
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ReiseKostOgLosji {

        private String persontype;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    private static class UtenlandskArtist {}
}
