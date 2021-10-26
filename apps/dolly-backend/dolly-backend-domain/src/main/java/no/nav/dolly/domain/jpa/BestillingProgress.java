package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Testident.Master;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BESTILLING_PROGRESS")
@Builder
public class BestillingProgress {

    @Id
    @GeneratedValue(generator = "bestillingProgressIdGenerator")
    @GenericGenerator(name = "bestillingProgressIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "BESTILLING_PROGRESS_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BESTILLING_ID", nullable = false)
    private Bestilling bestilling;

    private String ident;

    @Column(name = "TPSF_SUCCESS_ENVIRONMENTS")
    private String tpsfSuccessEnv;

    @Column(name = "SIGRUNSTUB_STATUS")
    private String sigrunstubStatus;

    @Column(name = "KRRSTUB_STATUS")
    private String krrstubStatus;

    @Column(name = "UDISTUB_STATUS")
    private String udistubStatus;

    @Column(name = "AAREG_STATUS")
    private String aaregStatus;

    @Column(name = "ARENAFORVALTER_STATUS")
    private String arenaforvalterStatus;

    @Column(name = "PDLFORVALTER_STATUS")
    private String pdlforvalterStatus;

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

    @Column(name = "SYKEMELDING_STATUS")
    private String sykemeldingStatus;

    @Column(name = "SKJERMINGSREGISTER_STATUS")
    private String skjermingsregisterStatus;

    @Column(name = "TPS_IMPORT_STATUS")
    private String tpsImportStatus;

    @Column(name = "PDL_IMPORT_STATUS")
    private String pdlImportStatus;

    @Column(name = "PDL_DATA_STATUS")
    private String pdlDataStatus;

    @Column(name = "master")
    @Enumerated(EnumType.STRING)
    private Master master;

    private String feil;

    public BestillingProgress(Bestilling bestilling, String ident, Master master) {
        this.ident = ident;
        this.bestilling = bestilling;
        this.master = master;
    }

    @JsonIgnore
    public boolean isTpsf() {
        return getMaster() == Master.TPSF;
    }

    @JsonIgnore
    public boolean isPdl() {
        return getMaster() == Master.PDL;
    }
}
