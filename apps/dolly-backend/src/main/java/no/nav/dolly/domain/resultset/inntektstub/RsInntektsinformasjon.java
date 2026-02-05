package no.nav.dolly.domain.resultset.inntektstub;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsInntektsinformasjon {

    @Schema(type = "Integer",
            description = "Antall måneder som denne inntektsinformasjon skal kopieres",
            example = "36")
    private Integer antallMaaneder;

    @Schema(description = "Siste år-måned for gjeldene inntektsinformasjon",
            example = "yyyy-MM",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String sisteAarMaaned;

    @Schema(description = "Organisasjonsnummer/norskIdent",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String opplysningspliktig;

    @Schema(description = "Organisasjonsnummer/norskIdent",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String virksomhet;

    private List<Inntekt> inntektsliste;

    private List<Fradrag> fradragsliste;

    private List<Forskuddstrekk> forskuddstrekksliste;

    private List<Historikk> historikk;

    private LocalDateTime rapporteringsdato;

    private Integer versjon;

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

    public List<Historikk> getHistorikk() {
        if (isNull(historikk)) {
            historikk = new ArrayList<>();
        }
        return historikk;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Fradrag {

        private Double beloep;

        @Schema(description = "Gyldige verdier finnes i kodeverket 'Fradragbeskrivelse'")
        private String beskrivelse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Forskuddstrekk {

        private Double beloep;

        @Schema(description = "Gyldige verdier finnes i kodeverket 'Forskuddstrekkbeskrivelse'")
        private String beskrivelse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Historikk {

        private List<Inntekt> inntektsliste;

        private List<Fradrag> fradragsliste;

        private List<Forskuddstrekk> forskuddstrekksliste;

        private LocalDateTime rapporteringsdato;
    }
}
