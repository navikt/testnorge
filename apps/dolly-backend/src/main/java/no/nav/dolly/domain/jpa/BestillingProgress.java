package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Testident.Master;
import org.apache.commons.lang3.StringUtils;
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
@Table("BESTILLING_PROGRESS")
@Builder
public class BestillingProgress implements Serializable {

    private static final int MAX_DOKARKIV_STATUS_LENGTH = 2000;

    @Id
    private Long id;

    @Version
    @Column("VERSJON")
    private Long versjon;

    @Column("BESTILLING_ID")
    private Long bestillingId;

    @Transient
    private Bestilling bestilling;

    @Column("IDENT")
    private String ident;

    @Column("SIGRUNSTUB_STATUS")
    private String sigrunstubStatus;

    @Column("KRRSTUB_STATUS")
    private String krrstubStatus;

    @Column("FULLMAKT_STATUS")
    private String fullmaktStatus;

    @Column("MEDL_STATUS")
    private String medlStatus;

    @Column("UDISTUB_STATUS")
    private String udistubStatus;

    @Column("AAREG_STATUS")
    private String aaregStatus;

    @Column("ARENAFORVALTER_STATUS")
    private String arenaforvalterStatus;

    @Column("INSTDATA_STATUS")
    private String instdataStatus;

    @Column("INNTEKTSSTUB_STATUS")
    private String inntektstubStatus;

    @Column("PENSJONFORVALTER_STATUS")
    private String pensjonforvalterStatus;

    @Column("INNTEKTSMELDING_STATUS")
    private String inntektsmeldingStatus;

    @Column("BREGSTUB_STATUS")
    private String brregstubStatus;

    @Column("DOKARKIV_STATUS")
    private String dokarkivStatus;

    @Column("HISTARK_STATUS")
    private String histarkStatus;

    @Column("SYKEMELDING_STATUS")
    private String sykemeldingStatus;

    @Column("SKJERMINGSREGISTER_STATUS")
    private String skjermingsregisterStatus;

    @Column("TPS_MESSAGING_STATUS")
    private String tpsMessagingStatus;

    @Column("PDL_IMPORT_STATUS")
    private String pdlImportStatus;

    @Column("PDL_FORVALTER_STATUS")
    private String pdlForvalterStatus;

    @Column("PDL_ORDRE_STATUS")
    private String pdlOrdreStatus;

    @Column("KONTOREGISTER_STATUS")
    private String kontoregisterStatus;

    @Column("PDL_PERSON_STATUS")
    private String pdlPersonStatus;

    @Column("ARBEIDSPLASSENCV_STATUS")
    private String arbeidsplassenCVStatus;

    @Column("SKATTEKORT_STATUS")
    private String skattekortStatus;

    @Column("YRKESSKADE_STATUS")
    private String yrkesskadeStatus;

    @Column("ARBEIDSSOEKERREGISTERET_STATUS")
    private String arbeidssoekerregisteretStatus;

    @Column("ETTERLATTE_STATUS")
    private  String etterlatteStatus;

    @Column("NOM_STATUS")
    private String nomStatus;

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

    public void setDokarkivStatus(String dokarkivStatus) {
        this.dokarkivStatus = StringUtils.left(dokarkivStatus, MAX_DOKARKIV_STATUS_LENGTH);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BestillingProgress that)) return false;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getVersjon(), that.getVersjon())
                .append(getBestillingId(), that.getBestillingId())
                .append(getIdent(), that.getIdent())
                .append(getSigrunstubStatus(), that.getSigrunstubStatus())
                .append(getFullmaktStatus(), that.getFullmaktStatus())
                .append(getKrrstubStatus(), that.getKrrstubStatus())
                .append(getMedlStatus(), that.getMedlStatus())
                .append(getUdistubStatus(), that.getUdistubStatus())
                .append(getAaregStatus(), that.getAaregStatus())
                .append(getArenaforvalterStatus(), that.getArenaforvalterStatus())
                .append(getInstdataStatus(), that.getInstdataStatus())
                .append(getInntektstubStatus(), that.getInntektstubStatus())
                .append(getPensjonforvalterStatus(), that.getPensjonforvalterStatus())
                .append(getInntektsmeldingStatus(), that.getInntektsmeldingStatus())
                .append(getBrregstubStatus(), that.getBrregstubStatus())
                .append(getDokarkivStatus(), that.getDokarkivStatus())
                .append(getHistarkStatus(), that.getHistarkStatus())
                .append(getSykemeldingStatus(), that.getSykemeldingStatus())
                .append(getSkjermingsregisterStatus(), that.getSkjermingsregisterStatus())
                .append(getTpsMessagingStatus(), that.getTpsMessagingStatus())
                .append(getPdlImportStatus(), that.getPdlImportStatus())
                .append(getPdlForvalterStatus(), that.getPdlForvalterStatus())
                .append(getPdlOrdreStatus(), that.getPdlOrdreStatus())
                .append(getKontoregisterStatus(), that.getKontoregisterStatus())
                .append(getPdlPersonStatus(), that.getPdlPersonStatus())
                .append(getArbeidsplassenCVStatus(), that.getArbeidsplassenCVStatus())
                .append(getSkattekortStatus(), that.getSkattekortStatus())
                .append(getYrkesskadeStatus(), that.getYrkesskadeStatus())
                .append(getArbeidssoekerregisteretStatus(), that.getArbeidssoekerregisteretStatus())
                .append(getEtterlatteStatus(), that.getEtterlatteStatus())
                .append(getMaster(), that.getMaster())
                .append(getFeil(), that.getFeil())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getVersjon())
                .append(getBestillingId())
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
                .append(getMaster())
                .append(getFeil())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "BestillingProgress{" +
                "id=" + id +
                ", versjon=" + versjon +
                ", bestilling=" + bestillingId +
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
                ", arbeidssoekerregisteretStatus=" + arbeidssoekerregisteretStatus + '\'' +
                ", etterlatteStatus='" + etterlatteStatus + '\'' +
                ", master=" + master +
                ", pdlSync=" + pdlSync +
                ", feil='" + feil + '\'' +
                '}';
    }
}
