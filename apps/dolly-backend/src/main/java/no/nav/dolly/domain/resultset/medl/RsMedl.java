package no.nav.dolly.domain.resultset.medl;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsMedl {


    @Schema(description = "Startdatoen for perioden til medlemskapsunntaket, på ISO-8601 format.")
    private LocalDate fraOgMed;

    @Schema(description = "Sluttdatoen for perioden til medlemskapsunntaket, på ISO-8601 format.")
    private LocalDate tilOgMed;

    @Schema(description = "Grunnlaget for dette medlemskapsunntaket.\n" +
            "Kodeverk: GrunnlagMedl")
    private String grunnlag;

    @Schema(description = "Dekningsgraden for dette medlemskapsunntaket.")
    private String dekning;

    @Schema(description = "Lovvalget for dette medlemskapsunntaket.")
    private String lovvalg;

    @Schema(description = "Landet dette medlemskapsunntaket gjelder for.")
    private String lovvalgsland;

    @Schema(description = "Status for perioden til medlemskapsunntaket.\n" +
            "Kodeverk: PeriodestatusMedl")
    private String status;


    @Schema(description = "Dersom statusen på medlemskapsunntaket ikke er gyldig vil dette feltet beskrive hvorfor.\n" +
            "Kodeverk: StatusaarsakMedl")
    private String statusaarsak;

    private String kilde;
    private String kildedokument;
    private Studieinformasjon studieinformasjon;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Studieinformasjon {

        private String statsborgerland;
        private String studieland;
        private Boolean delstudie;
        private Boolean soeknadInnvilget;
    }
}
