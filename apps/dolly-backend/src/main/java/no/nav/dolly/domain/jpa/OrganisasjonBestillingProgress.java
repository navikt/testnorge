package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organisasjon_bestilling_progress")
@Builder
public class OrganisasjonBestillingProgress {

    @Id
    @GeneratedValue(generator = "bestillingProgressIdGenerator")
    @GenericGenerator(name = "bestillingProgressIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "ORGANISASJON_BESTILLING_PROGRESS_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "organisasjonsnr")
    private String organisasjonsnummer;

    @Column(name = "org_forvalter_status")
    private String organisasjonsforvalterStatus;

    @ManyToOne
    @JoinColumn(name = "bestilling_id", nullable = false)
    private OrganisasjonBestilling bestilling;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisasjonBestillingProgress that = (OrganisasjonBestillingProgress) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(organisasjonsnummer, that.organisasjonsnummer)
                .append(organisasjonsforvalterStatus, that.organisasjonsforvalterStatus)
                .append(bestilling, that.bestilling)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisasjonsnummer)
                .append(organisasjonsforvalterStatus)
                .append(bestilling)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "OrganisasjonBestillingProgress{" +
                "id=" + id +
                ", organisasjonsnummer='" + organisasjonsnummer + '\'' +
                ", organisasjonsforvalterStatus='" + organisasjonsforvalterStatus + '\'' +
                ", bestilling=" + bestilling +
                '}';
    }
}
