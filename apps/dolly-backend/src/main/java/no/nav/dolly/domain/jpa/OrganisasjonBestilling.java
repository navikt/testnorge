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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("organisasjon_bestilling")
public class OrganisasjonBestilling {

    @Id
    private Long id;

    @Column("miljoer")
    private String miljoer;

    @Column("antall")
    private Integer antall;

    @Column("sist_oppdatert")
    private LocalDateTime sistOppdatert;

    @Column("feil")
    private String feil;

    @Column("ferdig")
    private Boolean ferdig;

    @Column("opprettet_fra_id")
    private Long opprettetFraId;

    @Column("best_kriterier")
    private String bestKriterier;

    @Column("bruker_id")
    private Long brukerId;

    @Transient
    private Bruker bruker;

    @Transient
    @Builder.Default
    private List<OrganisasjonBestillingProgress> progresser = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisasjonBestilling that = (OrganisasjonBestilling) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(miljoer, that.miljoer)
                .append(antall, that.antall)
                .append(sistOppdatert, that.sistOppdatert)
                .append(feil, that.feil)
                .append(ferdig, that.ferdig)
                .append(opprettetFraId, that.opprettetFraId)
                .append(bestKriterier, that.bestKriterier)
                .append(brukerId, that.brukerId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(miljoer)
                .append(antall)
                .append(sistOppdatert)
                .append(feil)
                .append(ferdig)
                .append(opprettetFraId)
                .append(bestKriterier)
                .append(brukerId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "OrganisasjonBestilling{" +
                "id=" + id +
                ", miljoer='" + miljoer + '\'' +
                ", antall=" + antall +
                ", sistOppdatert=" + sistOppdatert +
                ", feil='" + feil + '\'' +
                ", ferdig=" + ferdig +
                ", opprettetFraId=" + opprettetFraId +
                ", bestKriterier='" + bestKriterier + '\'' +
                ", brukerId=" + brukerId +
                '}';
    }
}