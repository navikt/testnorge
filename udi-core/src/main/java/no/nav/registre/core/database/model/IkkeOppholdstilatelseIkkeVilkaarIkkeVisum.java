package no.nav.registre.core.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett;
import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdstillatelse;
import no.udi.mt_1067_nav_data.v1.OppholdsgrunnlagKategori;
import no.udi.mt_1067_nav_data.v1.OvrigIkkeOppholdsKategori;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class IkkeOppholdstilatelseIkkeVilkaarIkkeVisum {

    @GeneratedValue
    @Id
    private long id;

    @Embedded
    private UtvistMedInnreiseForbud utvistMedInnreiseForbud;

    private Date avgjoerelsesDato;

    private Date bortfallAvPOellerBOS;

    private Date virkningsDato;

    private Date utreiseFrist;

    private OppholdsgrunnlagKategori avslagsGrunnlagOvrig;

    private EOSellerEFTAGrunnlagskategoriOppholdstillatelse avslagsGrunnlagEOS;

    private EOSellerEFTAGrunnlagskategoriOppholdsrett oppholdsrettRealitetsbehandlet;

    private Date formetltVedtakUtreiseFrist;

    private OvrigIkkeOppholdsKategori arsak;

    public void setVirkningsDato(Date date) {
        this.virkningsDato = new Date(date.toInstant().getEpochSecond());
    }

    public Date getVirkningsDato() {
        return new Date(this.virkningsDato.toInstant().getEpochSecond());
    }

    public void setFormetltVedtakUtreiseFrist(Date date) {
        this.formetltVedtakUtreiseFrist = new Date(date.toInstant().getEpochSecond());
    }

    public Date getFormetltVedtakUtreiseFrist() {
        return new Date(this.formetltVedtakUtreiseFrist.toInstant().getEpochSecond());
    }

    public void setBortfallAvPOellerBOS(Date date) {
        this.bortfallAvPOellerBOS = new Date(date.toInstant().getEpochSecond());
    }

    public Date getBortfallAvPOellerBOS() {
        return new Date(this.bortfallAvPOellerBOS.toInstant().getEpochSecond());
    }

    public void setAvgjoerelsesDato(Date date) {
        this.avgjoerelsesDato = new Date(date.toInstant().getEpochSecond());
    }

    public Date getAvgjoerelsesDato() {
        return new Date(this.avgjoerelsesDato.toInstant().getEpochSecond());
    }

    public void setUtreiseFrist(Date date) {
        this.utreiseFrist = new Date(date.toInstant().getEpochSecond());
    }

    public Date getUtreiseFrist() {
        return new Date(this.utreiseFrist.toInstant().getEpochSecond());
    }
}
