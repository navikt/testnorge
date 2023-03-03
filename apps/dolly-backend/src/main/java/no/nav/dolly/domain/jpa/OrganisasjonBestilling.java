package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organisasjon_bestilling")
public class OrganisasjonBestilling {

    @Id
    @GeneratedValue(generator = "organisasjonBestillingIdGenerator")
    @GenericGenerator(name = "organisasjonBestillingIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "ORGANISASJON_BESTILLING_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "miljoer", nullable = false)
    private String miljoer;

    @Column(name = "antall", nullable = false)
    private Integer antall;

    @Column(name = "sist_oppdatert", nullable = false)
    @UpdateTimestamp
    private LocalDateTime sistOppdatert;

    @Column(name = "feil")
    private String feil;

    @Column(name = "ferdig", columnDefinition = "Boolean default false")
    private Boolean ferdig;

    @Column(name = "opprettet_fra_id")
    private Long opprettetFraId;

    @Column(name = "mal_bestilling_navn")
    private String malBestillingNavn;

    @Column(name = "best_kriterier")
    private String bestKriterier;

    @ManyToOne
    @JoinColumn(name = "bruker_id", nullable = false)
    private Bruker bruker;

    @OneToMany(mappedBy = "bestilling", fetch = FetchType.LAZY)
    private List<OrganisasjonBestillingProgress> progresser;

    public List<OrganisasjonBestillingProgress> getProgresser() {
        if (isNull(progresser)) {
            progresser = new ArrayList<>();
        }
        return progresser;
    }
}