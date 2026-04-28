package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
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
@Table("bestilling_mal")
public class BestillingMal implements Serializable {

    @Id
    private Long id;

    @Column("best_kriterier")
    private String bestKriterier;

    @Column("miljoer")
    private String miljoer;

    @Column("mal_navn")
    private String malNavn;

    @Column("bruker_id")
    private Long brukerId;

    @Transient
    private RsBrukerUtenFavoritter bruker;

    @Column("sist_oppdatert")
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
        return "BestillingMal{" +
                "id=" + id +
                ", bestKriterier='" + bestKriterier + '\'' +
                ", miljoer='" + miljoer + '\'' +
                ", malNavn='" + malNavn + '\'' +
                ", bruker=" + brukerId +
                ", sistOppdatert=" + sistOppdatert +
                '}';
    }
}
