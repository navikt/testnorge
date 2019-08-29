package no.nav.dolly.domain.resultset.udistub.model.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvslagEllerBortfallTo {

    private LocalDate avgjorelsesDato;
    private LocalDate bortfallAvPOellerBOSDato;
    private LocalDate tilbakeKallVirkningsDato;
    private LocalDate tilbakeKallUtreiseFrist;
    private LocalDate avslagOppholdstillatelseUtreiseFrist;
    private String avslagGrunnlagOverig;
    private String avslagGrunnlagTillatelseGrunnlagEOS;
    private LocalDate avslagOppholdstillatelseBehandletUtreiseFrist;
    private String avslagOppholdstillatelseBehandletGrunnlagOvrig;
    private String avslagOppholdstillatelseBehandletGrunnlagEOS;
    private String avslagOppholdsrettBehandlet;
    private LocalDate formeltVedtakUtreiseFrist;

}
