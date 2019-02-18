package no.nav.dolly.domain.jpa;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Bestilling {

    @Id
    @GeneratedValue(generator = "bestillingIdGenerator")
    @GenericGenerator(name = "bestillingIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_BESTILLING_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @ManyToOne
    @JoinColumn(name = "GRUPPE_ID", nullable = false)
    private Testgruppe gruppe;

    @Column(name = "FERDIG", nullable = false)
    private boolean ferdig;

    @Column(name = "MILJOER", nullable = false)
    private String miljoer;

    @Column(name = "ANTALL_IDENTER", nullable = false)
    private int antallIdenter;

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

    @Column(name = "OPENAM_SENT")
    private String openamSent;
}