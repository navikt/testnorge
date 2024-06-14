package no.nav.pdl.forvalter.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "alias")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbAlias {

    private static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    @GeneratedValue(generator = "aliasIdGenerator")
    @GenericGenerator(name = "aliasIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "alias_sequence"),
            @Parameter(name = "increment_size", value = "1"),
    })
    private Long id;

    @UpdateTimestamp
    @Column(name = "sist_oppdatert")
    private LocalDateTime sistOppdatert;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false, updatable = false)
    private DbPerson person;

    private String tidligereIdent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbAlias dbAlias = (DbAlias) o;

        if (getId() != null ? !getId().equals(dbAlias.getId()) : dbAlias.getId() != null) return false;
        if (getSistOppdatert() != null ? !getSistOppdatert().equals(dbAlias.getSistOppdatert()) : dbAlias.getSistOppdatert() != null)
            return false;
        if (getPerson() != null ? !getPerson().equals(dbAlias.getPerson()) : dbAlias.getPerson() != null) return false;
        return getTidligereIdent() != null ? getTidligereIdent().equals(dbAlias.getTidligereIdent()) : dbAlias.getTidligereIdent() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getSistOppdatert() != null ? getSistOppdatert().hashCode() : 0);
        result = 31 * result + (getPerson() != null ? getPerson().hashCode() : 0);
        result = 31 * result + (getTidligereIdent() != null ? getTidligereIdent().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DbAlias{" +
                "id=" + id +
                ", sistOppdatert=" + sistOppdatert +
                ", person=" + person +
                ", tidligereIdent='" + tidligereIdent + '\'' +
                '}';
    }
}