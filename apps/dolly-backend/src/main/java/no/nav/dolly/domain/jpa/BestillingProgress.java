package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Testident.Master;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("bestilling_progress")
@Builder
public class BestillingProgress implements Serializable {

    @Id
    private Long id;

    @Version
    @Column("versjon")
    private Long versjon;

    @Column("bestilling_id")
    private Long bestillingId;

    @Transient
    private Bestilling bestilling;

    @Column("ident")
    private String ident;

    @Column("sigrunstub_status")
    private String sigrunstubStatus;

    @Column("krrstub_status")
    private String krrstubStatus;

    @Column("fullmakt_status")
    private String fullmaktStatus;

    @Column("medl_status")
    private String medlStatus;

    @Column("udistub_status")
    private String udistubStatus;

    @Column("aareg_status")
    private String aaregStatus;

    @Column("arenaforvalter_status")
    private String arenaforvalterStatus;

    @Column("instdata_status")
    private String instdataStatus;

    @Column("inntektsstub_status")
    private String inntektstubStatus;

    @Column("pensjonforvalter_status")
    private String pensjonforvalterStatus;

    @Column("inntektsmelding_status")
    private String inntektsmeldingStatus;

    @Column("bregstub_status")
    private String brregstubStatus;

    @Column("dokarkiv_status")
    private String dokarkivStatus;

    @Column("histark_status")
    private String histarkStatus;

    @Column("sykemelding_status")
    private String sykemeldingStatus;

    @Column("skjermingsregister_status")
    private String skjermingsregisterStatus;

    @Column("tps_messaging_status")
    private String tpsMessagingStatus;

    @Column("pdl_import_status")
    private String pdlImportStatus;

    @Column("pdl_forvalter_status")
    private String pdlForvalterStatus;

    @Column("pdl_ordre_status")
    private String pdlOrdreStatus;

    @Column("kontoregister_status")
    private String kontoregisterStatus;

    @Column("pdl_person_status")
    private String pdlPersonStatus;

    @Column("arbeidsplassencv_status")
    private String arbeidsplassenCVStatus;

    @Column("skattekort_status")
    private String skattekortStatus;

    @Column("yrkesskade_status")
    private String yrkesskadeStatus;

    @Column("arbeidssoekerregisteret_status")
    private String arbeidssoekerregisteretStatus;

    @Column("etterlatte_status")
    private  String etterlatteStatus;

    @Column("nom_status")
    private String nomStatus;

    @Column("kelvin_aap_status")
    private String kelvinAapStatus;

    @Column("oppfoelgingsvedtak14a_status")
    private String oppfoelgingsvedtak14aStatus;

    @Column("henvendelse_status")
    private String henvendelseStatus;

    @Column("master")
    private Master master;

    @Transient
    private boolean pdlSync;

    private String feil;

    public BestillingProgress(Long bestillingId, String ident, Master master) {
        this.ident = ident;
        this.bestillingId = bestillingId;
        this.master = master;
    }

    @JsonIgnore
    public boolean isPdlf() {
        return getMaster() == Master.PDLF;
    }

    @JsonIgnore
    public boolean isPdl() {
        return getMaster() == Master.PDL;
    }

    @JsonIgnore
    public boolean isIdentGyldig() {

        return isNotBlank(getIdent()) &&
                getIdent().length() == 11 &&
                (isPdl() || isPdlf());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BestillingProgress that = (BestillingProgress) o;

        return new EqualsBuilder().append(getId(),
                that.getId()).append(getVersjon(),
                that.getVersjon()).append(getBestillingId(),
                that.getBestillingId()).append(getBestilling(),
                that.getBestilling()).append(getIdent(),
                that.getIdent()).append(getSigrunstubStatus(),
                that.getSigrunstubStatus()).append(getKrrstubStatus(),
                that.getKrrstubStatus()).append(getFullmaktStatus(),
                that.getFullmaktStatus()).append(getMedlStatus(),
                that.getMedlStatus()).append(getUdistubStatus(),
                that.getUdistubStatus()).append(getAaregStatus(),
                that.getAaregStatus()).append(getArenaforvalterStatus(),
                that.getArenaforvalterStatus()).append(getInstdataStatus(),
                that.getInstdataStatus()).append(getInntektstubStatus(),
                that.getInntektstubStatus()).append(getPensjonforvalterStatus(),
                that.getPensjonforvalterStatus()).append(getInntektsmeldingStatus(),
                that.getInntektsmeldingStatus()).append(getBrregstubStatus(),
                that.getBrregstubStatus()).append(getDokarkivStatus(),
                that.getDokarkivStatus()).append(getHistarkStatus(),
                that.getHistarkStatus()).append(getSykemeldingStatus(),
                that.getSykemeldingStatus()).append(getSkjermingsregisterStatus(),
                that.getSkjermingsregisterStatus()).append(getTpsMessagingStatus(),
                that.getTpsMessagingStatus()).append(getPdlImportStatus(),
                that.getPdlImportStatus()).append(getPdlForvalterStatus(),
                that.getPdlForvalterStatus()).append(getPdlOrdreStatus(),
                that.getPdlOrdreStatus()).append(getKontoregisterStatus(),
                that.getKontoregisterStatus()).append(getPdlPersonStatus(),
                that.getPdlPersonStatus()).append(getArbeidsplassenCVStatus(),
                that.getArbeidsplassenCVStatus()).append(getSkattekortStatus(),
                that.getSkattekortStatus()).append(getYrkesskadeStatus(),
                that.getYrkesskadeStatus()).append(getArbeidssoekerregisteretStatus(),
                that.getArbeidssoekerregisteretStatus()).append(getEtterlatteStatus(),
                that.getEtterlatteStatus()).append(getNomStatus(),
                that.getNomStatus()).append(getKelvinAapStatus(),
                that.getKelvinAapStatus()).append(getOppfoelgingsvedtak14aStatus(),
                that.getOppfoelgingsvedtak14aStatus()).append(getHenvendelseStatus(),
                that.getHenvendelseStatus()).append(getMaster(),
                that.getMaster()).append(getFeil(),
                that.getFeil()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId()).append(getVersjon())
                .append(getBestillingId())
                .append(getBestilling())
                .append(getIdent())
                .append(getSigrunstubStatus())
                .append(getKrrstubStatus())
                .append(getFullmaktStatus())
                .append(getMedlStatus())
                .append(getUdistubStatus())
                .append(getAaregStatus())
                .append(getArenaforvalterStatus())
                .append(getInstdataStatus())
                .append(getInntektstubStatus())
                .append(getPensjonforvalterStatus())
                .append(getInntektsmeldingStatus())
                .append(getBrregstubStatus())
                .append(getDokarkivStatus())
                .append(getHistarkStatus())
                .append(getSykemeldingStatus())
                .append(getSkjermingsregisterStatus())
                .append(getTpsMessagingStatus())
                .append(getPdlImportStatus())
                .append(getPdlForvalterStatus())
                .append(getPdlOrdreStatus())
                .append(getKontoregisterStatus())
                .append(getPdlPersonStatus())
                .append(getArbeidsplassenCVStatus())
                .append(getSkattekortStatus())
                .append(getYrkesskadeStatus())
                .append(getArbeidssoekerregisteretStatus())
                .append(getEtterlatteStatus())
                .append(getNomStatus())
                .append(getKelvinAapStatus())
                .append(getOppfoelgingsvedtak14aStatus())
                .append(getHenvendelseStatus())
                .append(getMaster())
                .append(getFeil())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "BestillingProgress{" +
               "id=" + id +
               ", versjon=" + versjon +
               ", bestillingId=" + bestillingId +
               ", bestilling=" + bestilling +
               ", ident='" + ident + '\'' +
               ", sigrunstubStatus='" + sigrunstubStatus + '\'' +
               ", krrstubStatus='" + krrstubStatus + '\'' +
               ", fullmaktStatus='" + fullmaktStatus + '\'' +
               ", medlStatus='" + medlStatus + '\'' +
               ", udistubStatus='" + udistubStatus + '\'' +
               ", aaregStatus='" + aaregStatus + '\'' +
               ", arenaforvalterStatus='" + arenaforvalterStatus + '\'' +
               ", instdataStatus='" + instdataStatus + '\'' +
               ", inntektstubStatus='" + inntektstubStatus + '\'' +
               ", pensjonforvalterStatus='" + pensjonforvalterStatus + '\'' +
               ", inntektsmeldingStatus='" + inntektsmeldingStatus + '\'' +
               ", brregstubStatus='" + brregstubStatus + '\'' +
               ", dokarkivStatus='" + dokarkivStatus + '\'' +
               ", histarkStatus='" + histarkStatus + '\'' +
               ", sykemeldingStatus='" + sykemeldingStatus + '\'' +
               ", skjermingsregisterStatus='" + skjermingsregisterStatus + '\'' +
               ", tpsMessagingStatus='" + tpsMessagingStatus + '\'' +
               ", pdlImportStatus='" + pdlImportStatus + '\'' +
               ", pdlForvalterStatus='" + pdlForvalterStatus + '\'' +
               ", pdlOrdreStatus='" + pdlOrdreStatus + '\'' +
               ", kontoregisterStatus='" + kontoregisterStatus + '\'' +
               ", pdlPersonStatus='" + pdlPersonStatus + '\'' +
               ", arbeidsplassenCVStatus='" + arbeidsplassenCVStatus + '\'' +
               ", skattekortStatus='" + skattekortStatus + '\'' +
               ", yrkesskadeStatus='" + yrkesskadeStatus + '\'' +
               ", arbeidssoekerregisteretStatus='" + arbeidssoekerregisteretStatus + '\'' +
               ", etterlatteStatus='" + etterlatteStatus + '\'' +
               ", nomStatus='" + nomStatus + '\'' +
               ", kelvinAapStatus='" + kelvinAapStatus + '\'' +
               ", oppfoelgingsvedtak14aStatus='" + oppfoelgingsvedtak14aStatus + '\'' +
               ", henvendelseStatus='" + henvendelseStatus + '\'' +
               ", master=" + master +
               ", pdlSync=" + pdlSync +
               ", feil='" + feil + '\'' +
               '}';
    }
}
