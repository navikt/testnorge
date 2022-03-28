package no.nav.dolly.domain.resultset.inntektstub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonIgnore
    public boolean isEmpty() {

        return Stream.of(
                        getBilOgBaat(),
                        getBonusFraForsvaret(),
                        getDagmammaIEgenBolig(),
                        getEtterbetalingsperiode(),
                        getInntektPaaNorskKontinentalsokkel(),
                        getInntjeningsforhold(),
                        getLivrente(),
                        getLottOgPart(),
                        getNettoloenn(),
                        getPensjon(),
                        getReiseKostOgLosji(),
                        getUtenlandskArtist())
                .allMatch(Objects::isNull);
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class BilOgBaat {
    }

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
    public static class DagmammaIEgenBolig {
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periode {

        @Schema(type = "LocalDateTime",
                example = "yyyy-MM-dd")
        private LocalDateTime startdato;

        @Schema(type = "LocalDateTime",
                example = "yyyy-MM-dd")
        private LocalDateTime sluttdato;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class NorskKontinentalsokkel {
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inntjeningsforhold {

        private String inntjeningsforhold;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Livrente {
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class LottOgPartInnenFiske {
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Nettoloennsordning {
    }

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

        @Schema(description = "Verdier hentes fra kodeverk 'PersontypeForReiseKostLosji'"
        )
        private String persontype;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class UtenlandskArtist {
    }
}
