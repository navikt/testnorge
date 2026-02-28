package no.nav.pdl.forvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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

    @Column
    private Long personId;

    @Column
    private Long relatertPersonId;

    @Transient
    private DbPerson person;

    @Transient
    private DbPerson relatertPerson;

    @Version
    private Integer versjon;

    public boolean hasGammelIdentitet() {
        return relasjonType == RelasjonType.GAMMEL_IDENTITET;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DbRelasjon that)) return false;

        return new EqualsBuilder().append(id, that.id).append(relasjonType, that.relasjonType).append(personId, that.personId).append(relatertPersonId, that.relatertPersonId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(relasjonType).append(personId).append(relatertPersonId).toHashCode();
    }

    @Override
    public String toString() {
        return "DbRelasjon{" +
                "id=" + id +
                ", sistOppdatert=" + sistOppdatert +
                ", relasjonType=" + relasjonType +
                ", personId=" + personId +
                ", relatertPersonId=" + relatertPersonId +
                ", person=" + person +
                ", relatertPerson=" + relatertPerson +
                ", versjon=" + versjon +
                '}';
    }
}