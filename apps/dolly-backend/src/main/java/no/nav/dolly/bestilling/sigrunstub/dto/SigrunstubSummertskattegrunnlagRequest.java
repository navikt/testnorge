package no.nav.dolly.bestilling.sigrunstub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigrunstubSummertskattegrunnlagRequest {

    private List<SummertSkattegrunnlag> summertSkattegrunnlag;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummertSkattegrunnlag {

        private LocalDateTime ajourholdstidspunkt;
        private List<no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag.Grunnlag> grunnlag;
        private String inntektsaar;
        private List<no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag.KildeskattPaaLoennGrunnlag> kildeskattPaaLoennGrunnlag;
        private LocalDate skatteoppgjoersdato;
        private String personidentifikator;
        private Boolean skjermet;
        private String stadie;
        private List<no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag.SvalbardGrunnlag> svalbardGrunnlag;
    }
}
