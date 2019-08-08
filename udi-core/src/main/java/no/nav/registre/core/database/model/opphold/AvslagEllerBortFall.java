package no.nav.registre.core.database.model.opphold;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.OppholdsgrunnlagKategori;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.sql.Date;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AvslagEllerBortFall {

    private Date avgjorelsesDato;
    private Date bortfallAvPOellerBOSDato;

    private Date tilbakeKallVirkningsDato;
    private Date tilbakeKallUtreiseFrist;

    private Date avslagOppholdstillatelseUtreiseFrist;
    @AttributeOverride(name = "value", column = @Column(name = "avslag_grunnlag_overig"))
    private OppholdsgrunnlagKategori avslagGrunnlagOverig;
    @AttributeOverride(name = "value", column = @Column(name = "avslag_grunnlag_oppholdstillatelse"))
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse avslagGrunnlagTillatelseGrunnlagEOS;

    private Date avslagOppholdstillatelseBehandletUtreiseFrist;
    @AttributeOverride(name = "value", column = @Column(name = "avslag_oppholdstillatelse_behandlet_grunnlag_overig"))
    private OppholdsgrunnlagKategori avslagOppholdstillatelseBehandletGrunnlagOvrig;

    @AttributeOverride(name = "value", column = @Column(name = "avslag_oppholdstillatelse_behandlet_grunnlag_eos"))
    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse avslagOppholdstillatelseBehandletGrunnlagEOS;

    @AttributeOverride(name = "value", column = @Column(name = "avslag_oppholdsrett_behandlet"))
    private EOSellerEFTAGrunnlagskategoriOppholdsrett avslagOppholdsrettBehandlet;

    private Date formeltVedtakUtreiseFrist;
}
