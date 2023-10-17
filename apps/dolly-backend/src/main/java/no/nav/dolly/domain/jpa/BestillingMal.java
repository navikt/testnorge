package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BESTILLING_MAL")
public class BestillingMal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BEST_KRITERIER", nullable = false)
    private String bestKriterier;

    @Column(name = "MILJOER")
    private String miljoer;

    @Column(name = "MAL_NAVN", nullable = false)
    @OrderColumn
    private String malNavn;

    @ManyToOne
    @JoinColumn(name = "BRUKER_ID")
    private Bruker bruker;

    @Column(name = "SIST_OPPDATERT", nullable = false)
    @UpdateTimestamp
    private LocalDateTime sistOppdatert;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BestillingMal that = (BestillingMal) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(bestKriterier, that.bestKriterier)
                .append(miljoer, that.miljoer)
                .append(malNavn, that.malNavn)
                .append(bruker, that.bruker)
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
                .append(bruker)
                .append(sistOppdatert)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "BestillingMal{" +
                "id=" + id +
                ", bestKriterier='" + bestKriterier + '\'' +
                ", miljoer='" + miljoer + '\'' +
                ", malNavn='" + malNavn + '\'' +
                ", bruker=" + bruker +
                ", sistOppdatert=" + sistOppdatert +
                '}';
    }
}
