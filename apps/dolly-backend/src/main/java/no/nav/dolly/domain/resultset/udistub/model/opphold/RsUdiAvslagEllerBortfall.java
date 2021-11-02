package no.nav.dolly.domain.resultset.udistub.model.opphold;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsUdiAvslagEllerBortfall {

    private LocalDateTime avgjorelsesDato;

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
