package no.nav.dolly.domain.jpa.oracle;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.postgres.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_BESTILLING")
public class OraBestilling {

    @Id
    @GeneratedValue(generator = "oraBestillingIdGenerator")
    @GenericGenerator(name = "oraBestillingIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_BESTILLING_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @ManyToOne
    @JoinColumn(name = "GRUPPE_ID", nullable = false)
    private OraTestgruppe gruppe;

    @Column(name = "FERDIG", nullable = false)
    private boolean ferdig;

    @Column(name = "MILJOER", nullable = false)
    private String miljoer;

    @Column(name = "ANTALL_IDENTER", nullable = false)
    private Integer antallIdenter;

    @Column(name = "SIST_OPPDATERT", nullable = false)
    @UpdateTimestamp
    private LocalDateTime sistOppdatert;

    @Column(name = "STOPPET")
    private boolean stoppet;

    @Column(name = "FEIL")
    private String feil;

    @Column(name = "OPPRETTET_FRA_ID")
    private Long opprettetFraId;

    @Column(name = "TPSF_KRITERIER")
    private String tpsfKriterier;

    @Column(name = "BEST_KRITERIER")
    private String bestKriterier;

    @Column(name = "OPENAM_SENT")
    private String openamSent;

    @Column(name = "OPPRETT_FRA_IDENTER")
    private String opprettFraIdenter;

    @Column(name = "MAL_BESTILLING_NAVN")
    private String malBestillingNavn;

    @Column(name = "IDENT")
    private String ident;

    @ManyToOne
    @JoinColumn(name = "BRUKER_ID", nullable = false)
    private OraBruker bruker;

    @Column(name = "TPS_IMPORT")
    private String tpsImport;

    @Column(name = "KILDE_MILJOE")
    private String kildeMiljoe;

    @OneToMany(mappedBy = "bestillingId", fetch = FetchType.LAZY)
    private List<OraBestillingProgress> progresser;

    @OneToMany(mappedBy = "bestillingId", fetch = FetchType.LAZY)
    private List<OraBestillingKontroll> kontroller;

    @OneToMany(mappedBy = "bestillingId", fetch = FetchType.LAZY)
    private List<OraTransaksjonMapping> transaksjonmapping;

    public List<OraBestillingProgress> getProgresser() {
        if (isNull(progresser)) {
            progresser = new ArrayList<>();
        }
        return progresser;
    }

    public List<OraBestillingKontroll> getKontroller() {
        if (isNull(kontroller)) {
            kontroller = new ArrayList<>();
        }
        return kontroller;
    }

    public List<OraTransaksjonMapping> getTransaksjonmapping() {
        if (isNull(transaksjonmapping)) {
            transaksjonmapping = new ArrayList<>();
        }
        return transaksjonmapping;
    }
}