package no.nav.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Column(name="SIST_OPPDATERT" , nullable = false)
    private LocalDateTime sistOppdatert;

    public Bestilling(Testgruppe gruppe, int antallIdenter, LocalDateTime sistOppdatert, String miljoer){
        this.gruppe = gruppe;
        this.antallIdenter = antallIdenter;
        this.sistOppdatert = sistOppdatert;
        this.miljoer = miljoer;
    }
}
