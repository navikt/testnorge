package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Testident.Master;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BESTILLING_PROGRESS")
@Builder
public class BestillingProgress implements Serializable {

    private static final int MAX_DOKARKIV_STATUS_LENGTH = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "VERSJON")
    private Long versjon;

    @ManyToOne
    @JoinColumn(name = "BESTILLING_ID", nullable = false)
    private Bestilling bestilling;

    private String ident;

    @Column(name = "SIGRUNSTUB_STATUS")
    private String sigrunstubStatus;

    @Column(name = "KRRSTUB_STATUS")
    private String krrstubStatus;

    @Column(name = "FULLMAKT_STATUS")
    private String fullmaktStatus;

    @Column(name = "MEDL_STATUS")
    private String medlStatus;

    @Column(name = "UDISTUB_STATUS")
    private String udistubStatus;

    @Column(name = "AAREG_STATUS")
    private String aaregStatus;

    @Column(name = "ARENAFORVALTER_STATUS")
    private String arenaforvalterStatus;

    @Column(name = "INSTDATA_STATUS")
    private String instdataStatus;

    @Column(name = "INNTEKTSSTUB_STATUS")
    private String inntektstubStatus;

    @Column(name = "PENSJONFORVALTER_STATUS")
    private String pensjonforvalterStatus;

    @Column(name = "INNTEKTSMELDING_STATUS")
    private String inntektsmeldingStatus;

    @Column(name = "BREGSTUB_STATUS")
    private String brregstubStatus;

    @Column(name = "DOKARKIV_STATUS")
    private String dokarkivStatus;

    @Column(name = "HISTARK_STATUS")
    private String histarkStatus;

    @Column(name = "SYKEMELDING_STATUS")
    private String sykemeldingStatus;

    @Column(name = "SKJERMINGSREGISTER_STATUS")
    private String skjermingsregisterStatus;

    @Column(name = "TPS_MESSAGING_STATUS")
    private String tpsMessagingStatus;

    @Column(name = "PDL_IMPORT_STATUS")
    private String pdlImportStatus;

    @Column(name = "PDL_FORVALTER_STATUS")
    private String pdlForvalterStatus;

    @Column(name = "PDL_ORDRE_STATUS")
    private String pdlOrdreStatus;

    @Column(name = "KONTOREGISTER_STATUS")
    private String kontoregisterStatus;

    @Column(name = "PDL_PERSON_STATUS")
    private String pdlPersonStatus;

    @Column(name = "ARBEIDSPLASSENCV_STATUS")
    private String arbeidsplassenCVStatus;

    @Column(name = "SKATTEKORT_STATUS")
    private String skattekortStatus;

    @Column(name = "YRKESSKADE_STATUS")
    private String yrkesskadeStatus;

    @Column(name = "ARBEIDSSOEKERREGISTERET_STATUS")
    private String arbeidssoekerregisteretStatus;

    @Column(name = "master")
    @Enumerated(EnumType.STRING)
    private Master master;

    @Transient
    private boolean isPdlSync;

    private String feil;

    public BestillingProgress(Bestilling bestilling, String ident, Master master) {
        this.ident = ident;
        this.bestilling = bestilling;
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
                .append(getBestilling(), that.getBestilling())
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
                .append(getMaster(), that.getMaster())
                .append(getFeil(), that.getFeil())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getVersjon())
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
                .append(getMaster())
                .append(getFeil())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "BestillingProgress{" +
                "id=" + id +
                ", versjon=" + versjon +
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
                ", master=" + master +
                ", isPdlSync=" + isPdlSync +
                ", feil='" + feil + '\'' +
                '}';
    }
}
