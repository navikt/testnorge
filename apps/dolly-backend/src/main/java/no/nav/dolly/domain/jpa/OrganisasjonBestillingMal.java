package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORGANISAJON_BESTILLING_MAL")
public class OrganisasjonBestillingMal implements Serializable {

    @Id
    @GeneratedValue(generator = "OrganisasjonBestillingMalIdGenerator")
    @GenericGenerator(name = "OrganisasjonBestillingMalIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "ORGANISASJON_BESTILLING_MAL_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "BESTILLING_ID", nullable = false)
    private Integer BestillingId;

    @Column(name = "MAL_BESTILLING_NAVN", nullable = false)
    private Integer malBestillingNavn;

    @Column(name = "OPPRETTET_AV_ID", nullable = false)
    private Integer opprettetAvId;
}
