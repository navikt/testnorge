package no.nav.dolly.domain.resultset.udistub.model.opphold;

import java.time.LocalDateTime;

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
public class RsUdiAvslagEllerBortfall {

    private LocalDateTime avgjorelsesDato;

    private UdiGrunnlagOverig avslagGrunnlagOverig;
    private UdiGrunnlagEos avslagGrunnlagTillatelseGrunnlagEOS;
    private UdiOppholdsrettType avslagOppholdsrettBehandlet;
    private UdiGrunnlagEos avslagOppholdstillatelseBehandletGrunnlagEOS;
    private UdiGrunnlagOverig avslagOppholdstillatelseBehandletGrunnlagOvrig;

    private LocalDateTime avslagOppholdstillatelseBehandletUtreiseFrist;
    private LocalDateTime avslagOppholdstillatelseUtreiseFrist;
    private LocalDateTime bortfallAvPOellerBOSDato;
    private LocalDateTime tilbakeKallUtreiseFrist;
    private LocalDateTime formeltVedtakUtreiseFrist;
    private LocalDateTime tilbakeKallVirkningsDato;
}
