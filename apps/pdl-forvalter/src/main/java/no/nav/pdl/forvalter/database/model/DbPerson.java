package no.nav.pdl.forvalter.database.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.database.JSONUserType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Table("person")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbPerson {

    private static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    private Long id;

    @Column
    private LocalDateTime sistOppdatert;

    @Column
    private String ident;

    @Column
    private String fornavn;

    @Column
    private String mellomnavn;

    @Column
    private String etternavn;

    @Column
    @JsonSubTypes.Type(JSONUserType.class)
    private PersonDTO person;

    @Version
    private Integer versjon;

    @Transient
    private List<DbRelasjon> relasjoner;

    @Transient
    private List<DbAlias> alias;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DbPerson dbPerson)) return false;

        return new EqualsBuilder().append(id, dbPerson.id).append(ident, dbPerson.ident).append(fornavn, dbPerson.fornavn).append(mellomnavn, dbPerson.mellomnavn).append(etternavn, dbPerson.etternavn).append(person, dbPerson.person).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(ident).append(fornavn).append(mellomnavn).append(etternavn).append(person).toHashCode();
    }

    @Override
    public String toString() {
        return "DbPerson{" +
                "id=" + id +
                ", sistOppdatert=" + sistOppdatert +
                ", ident='" + ident + '\'' +
                ", fornavn='" + fornavn + '\'' +
                ", mellomnavn='" + mellomnavn + '\'' +
                ", etternavn='" + etternavn + '\'' +
                ", person=" + person +
                ", versjon=" + versjon +
                ", relasjoner=" + relasjoner +
                ", alias=" + alias +
                '}';
    }
}