package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("BRUKER")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Bruker implements Serializable {

    @Id
    private Long id;

    @Version
    @Column("VERSJON")
    private Long versjon;

    @Column("BRUKER_ID")
    private String brukerId;

    @Column("BRUKERNAVN")
    private String brukernavn;

    @Column("EPOST")
    private String epost;

    @Column("BRUKERTYPE")
    private Brukertype brukertype;

    @Column("REPRESENTERER_TEAM")
    private Long representererTeam;

    @Transient
    private Set<Team> teamMedlemskap = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Bruker bruker = (Bruker) o;

        return new EqualsBuilder()
                .append(id, bruker.id)
                .append(versjon, bruker.versjon)
                .append(brukerId, bruker.brukerId)
                .append(brukernavn, bruker.brukernavn)
                .append(epost, bruker.epost)
                .append(brukertype, bruker.brukertype)
                .append(representererTeam, bruker.representererTeam)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(versjon)
                .append(brukerId)
                .append(brukernavn)
                .append(epost)
                .append(brukertype)
                .append(representererTeam)
                .toHashCode();
    }

    public enum Brukertype {AZURE, BANKID, TEAM}

    @Override
    public String toString() {
        return "Bruker{" +
                "id=" + id +
                ", versjon=" + versjon +
                ", brukerId='" + brukerId + '\'' +
                ", brukernavn='" + brukernavn + '\'' +
                ", epost='" + epost + '\'' +
                ", brukertype=" + brukertype +
                ", representererTeam='" + representererTeam + '\'' +
                '}';
    }
}
