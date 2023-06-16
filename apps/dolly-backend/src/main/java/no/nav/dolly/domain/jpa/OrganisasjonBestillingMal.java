package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORGANISASJON_BESTILLING_MAL")
public class OrganisasjonBestillingMal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BEST_KRITERIER", nullable = false)
    private String bestKriterier;

    @Column(name = "MILJOER")
    private String miljoer;

    @Column(name = "MAL_BESTILLING_NAVN", nullable = false)
    private String malBestillingNavn;

    @ManyToOne
    @JoinColumn(name = "BRUKER_ID")
    private Bruker bruker;

    @Column(name = "SIST_OPPDATERT", nullable = false)
    @UpdateTimestamp
    private LocalDateTime sistOppdatert;
}