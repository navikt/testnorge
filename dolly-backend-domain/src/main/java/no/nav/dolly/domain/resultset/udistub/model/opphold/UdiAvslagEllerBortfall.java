package no.nav.dolly.domain.resultset.udistub.model.opphold;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UdiAvslagEllerBortfall {

    private LocalDate avgjorelsesDato;

    private UdiGrunnlagOverig avslagGrunnlagOverig;
    private UdiGrunnlagEos avslagGrunnlagTillatelseGrunnlagEOS;
    private UdiOppholdsrettType avslagOppholdsrettBehandlet;
    private UdiGrunnlagEos avslagOppholdstillatelseBehandletGrunnlagEOS;
    private UdiGrunnlagOverig avslagOppholdstillatelseBehandletGrunnlagOvrig;

    private LocalDate avslagOppholdstillatelseBehandletUtreiseFrist;
    private LocalDate avslagOppholdstillatelseUtreiseFrist;
    private LocalDate bortfallAvPOellerBOSDato;
    private LocalDate tilbakeKallUtreiseFrist;
    private LocalDate formeltVedtakUtreiseFrist;
    private LocalDate tilbakeKallVirkningsDato;
}
