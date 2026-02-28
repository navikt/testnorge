package no.nav.pdl.forvalter.database.model;

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

import java.time.LocalDateTime;

@Getter
@Setter
@Table("alias")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbAlias {

    private static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    private Long id;

    @Column
    private LocalDateTime sistOppdatert;

    @Transient
    private DbPerson person;

    @Column
    private Long personId;

    @Column
    private String tidligereIdent;

    @Version
    private Integer versjon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DbAlias dbAlias)) return false;

        return new EqualsBuilder().append(id, dbAlias.id).append(personId, dbAlias.personId).append(tidligereIdent, dbAlias.tidligereIdent).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(personId).append(tidligereIdent).toHashCode();
    }

    @Override
    public String toString() {
        return "DbAlias{" +
                "id=" + id +
                ", sistOppdatert=" + sistOppdatert +
                ", person=" + person +
                ", personId=" + personId +
                ", tidligereIdent='" + tidligereIdent + '\'' +
                ", versjon=" + versjon +
                '}';
    }
}