package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("organisasjon_bestilling_progress")
@Builder
public class OrganisasjonBestillingProgress {

    @Id
    private Long id;

    @Column("organisasjonsnr")
    private String organisasjonsnummer;

    @Column("org_forvalter_status")
    private String organisasjonsforvalterStatus;

//    @ManyToOne
//    @JoinColumn("bestilling_id", nullable = false)
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
