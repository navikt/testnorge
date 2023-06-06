package no.nav.dolly.domain.resultset.medl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedlData {
    private String id;
    private String ident;

    private LocalDate fraOgMed;
    private LocalDate tilOgMed;
    private String grunnlag;
    private String dekning;
    private String lovvalg;
    private String lovvalgsland;
    private String status;
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