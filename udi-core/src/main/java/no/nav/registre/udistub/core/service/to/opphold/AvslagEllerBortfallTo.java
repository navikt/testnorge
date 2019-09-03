package no.nav.registre.udistub.core.service.to.opphold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.OppholdsgrunnlagKategori;

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
    private OppholdsgrunnlagKategori avslagGrunnlagOverig;
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse avslagGrunnlagTillatelseGrunnlagEOS;
    private LocalDate avslagOppholdstillatelseBehandletUtreiseFrist;
    private OppholdsgrunnlagKategori avslagOppholdstillatelseBehandletGrunnlagOvrig;
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse avslagOppholdstillatelseBehandletGrunnlagEOS;
    private EOSellerEFTAGrunnlagskategoriOppholdsrett avslagOppholdsrettBehandlet;
    private LocalDate formeltVedtakUtreiseFrist;
}
