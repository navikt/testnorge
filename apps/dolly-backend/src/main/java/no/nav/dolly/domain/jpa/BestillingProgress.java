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

    @Column(name = "TPS_SYNC_STATUS")
    private String tpsSyncStatus;

    @Column(name = "ARBEIDSPLASSENCV_STATUS")
    private String arbeidsplassenCVStatus;

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

    //Husk å regenerere disse når nye systemer legges til
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BestillingProgress that = (BestillingProgress) o;

        return new EqualsBuilder().append(id, that.id)
                .append(versjon, that.versjon)
                .append(bestilling, that.bestilling)
                .append(ident, that.ident)
                .append(sigrunstubStatus, that.sigrunstubStatus)
                .append(krrstubStatus, that.krrstubStatus)
                .append(medlStatus, that.medlStatus)
                .append(udistubStatus, that.udistubStatus)
                .append(aaregStatus, that.aaregStatus)
                .append(arenaforvalterStatus, that.arenaforvalterStatus)
                .append(instdataStatus, that.instdataStatus)
                .append(inntektstubStatus, that.inntektstubStatus)
                .append(pensjonforvalterStatus, that.pensjonforvalterStatus)
                .append(inntektsmeldingStatus, that.inntektsmeldingStatus)
                .append(brregstubStatus, that.brregstubStatus)
                .append(dokarkivStatus, that.dokarkivStatus)
                .append(histarkStatus, that.histarkStatus)
                .append(sykemeldingStatus, that.sykemeldingStatus)
                .append(skjermingsregisterStatus, that.skjermingsregisterStatus)
                .append(tpsMessagingStatus, that.tpsMessagingStatus)
                .append(pdlImportStatus, that.pdlImportStatus)
                .append(pdlForvalterStatus, that.pdlForvalterStatus)
                .append(pdlOrdreStatus, that.pdlOrdreStatus)
                .append(kontoregisterStatus, that.kontoregisterStatus)
                .append(pdlPersonStatus, that.pdlPersonStatus)
                .append(arbeidsplassenCVStatus, that.arbeidsplassenCVStatus)
                .append(master, that.master)
                .append(feil, that.feil)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(versjon)
                .append(bestilling)
                .append(ident).append(sigrunstubStatus).append(krrstubStatus)
                .append(medlStatus)
                .append(udistubStatus).append(aaregStatus)
                .append(arenaforvalterStatus).append(instdataStatus)
                .append(inntektstubStatus)
                .append(pensjonforvalterStatus)
                .append(inntektsmeldingStatus).append(brregstubStatus)
                .append(dokarkivStatus)
                .append(histarkStatus)
                .append(sykemeldingStatus)
                .append(skjermingsregisterStatus)
                .append(tpsMessagingStatus)
                .append(pdlImportStatus)
                .append(pdlForvalterStatus)
                .append(pdlOrdreStatus)
                .append(kontoregisterStatus)
                .append(pdlPersonStatus).append(arbeidsplassenCVStatus)
                .append(master).append(feil).toHashCode();
    }

    @Override
    public String toString() {
        return "BestillingProgress{" +
                "id=" + id +
                ", versjon=" + versjon +
                ", bestilling=" + bestilling.getId() +
                ", gruppe=" + bestilling.getGruppe().getId() +
                ", ident='" + ident + '\'' +
                ", sigrunstubStatus='" + sigrunstubStatus + '\'' +
                ", krrstubStatus='" + krrstubStatus + '\'' +
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
