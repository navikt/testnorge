package no.nav.pdl.forvalter.database.model;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table("relasjon")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbRelasjon {

    private static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    private Long id;

    @Column
    private LocalDateTime sistOppdatert;

    @Column
    private RelasjonType relasjonType;

    @ManyToOne(foreignKey = "person_id")
    private DbPerson person;

    @ManyToOne(foreignKey = "relatert_person_id")
    private DbPerson relatertPerson;

    @Version
    private Integer versjon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DbRelasjon that)) return false;

        return new EqualsBuilder().append(id, that.id).append(sistOppdatert, that.sistOppdatert).append(relasjonType, that.relasjonType).append(person, that.person).append(relatertPerson, that.relatertPerson).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(sistOppdatert).append(relasjonType).append(person).append(relatertPerson).toHashCode();
    }

    @Override
    public String toString() {
        return "DbRelasjon{" +
               "id=" + id +
               ", sistOppdatert=" + sistOppdatert +
               ", relasjonType=" + relasjonType +
               ", person=" + person +
               ", relatertPerson=" + relatertPerson +
               ", versjon=" + versjon +
               '}';
    }
}