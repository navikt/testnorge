package no.nav.dolly.domain.jpa;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "BESTILLING_ID")
    private Long bestillingId;

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

    private String feil;

    public BestillingProgress(Long bestillingId, String ident) {
        this.ident = ident;
        this.bestillingId = bestillingId;
    }

}
