package no.nav.dolly.domain.resultset.inntektstub;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tilleggsinformasjon {

    private BilOgBaat bilOgBaat;
    private BonusFraForsvaret bonusFraForsvaret;
    private DagmammaIEgenBolig dagmammaIEgenBolig;
    private Periode etterbetalingsperiode;
    private NorskKontinentalsokkel inntektPaaNorskKontinentalsokkel;
    private SpesielleInntjeningsforhold  spesielleInntjeningsforhold ;
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
    public static class BilOgBaat {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BonusFraForsvaret {

        private String aaretUtbetalingenGjelderFor;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class DagmammaIEgenBolig {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periode {

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
    public static class NorskKontinentalsokkel {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpesielleInntjeningsforhold  {

        private String spesielleInntjeningsforhold ;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Livrente {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class LottOgPartInnenFiske {}

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Nettoloennsordning {}

    @Getter
    @Setter
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

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReiseKostOgLosji {

        @ApiModelProperty(
                position = 1,
                value = "Verdier hentes fra kodeverk 'PersontypeForReiseKostLosji'"
        )
        private String persontype;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class UtenlandskArtist {}
}
