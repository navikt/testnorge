package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ORGANISASJON_BESTILLING_MAL")
public class OrganisasjonBestillingMal implements Serializable {

    @Id
    private Long id;

    @Column("BEST_KRITERIER")
    private String bestKriterier;

    @Column("MILJOER")
    private String miljoer;

    @Column("MAL_NAVN")
//    @OrderColumn
    private String malNavn;

//    @ManyToOne
//    @JoinColumn("BRUKER_ID")
    @Column("BRUKER_ID")
    private Long brukerId;

    @Transient
    private Bruker bruker;

    @Column("SIST_OPPDATERT")
//    @UpdateTimestamp
    private LocalDateTime sistOppdatert;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisasjonBestillingMal that = (OrganisasjonBestillingMal) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(bestKriterier, that.bestKriterier)
                .append(miljoer, that.miljoer)
                .append(malNavn, that.malNavn)
                .append(brukerId, that.brukerId)
                .append(sistOppdatert, that.sistOppdatert)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(bestKriterier)
                .append(miljoer)
                .append(malNavn)
                .append(brukerId)
                .append(sistOppdatert)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "OrganisasjonBestillingMal{" +
                "id=" + id +
                ", bestKriterier='" + bestKriterier + '\'' +
                ", miljoer='" + miljoer + '\'' +
                ", malNavn='" + malNavn + '\'' +
                ", brukerId=" + brukerId +
                ", sistOppdatert=" + sistOppdatert +
                '}';
    }
}