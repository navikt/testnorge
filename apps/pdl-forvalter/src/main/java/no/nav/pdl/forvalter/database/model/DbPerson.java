package no.nav.pdl.forvalter.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.database.JSONUserType;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "person")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbPerson {

    private static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    @GeneratedValue(generator = "personIdGenerator")
    @GenericGenerator(name = "personIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "person_sequence"),
            @Parameter(name = "increment_size", value = "1"),
    })
    private Long id;

    @UpdateTimestamp
    @Column(name = "sist_oppdatert")
    private LocalDateTime sistOppdatert;
    private String ident;

    private String fornavn;
    private String mellomnavn;
    private String etternavn;

    @Type(JSONUserType.class)
    private PersonDTO person;

    @OrderBy("relasjonType desc, id desc")
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DbRelasjon> relasjoner = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DbAlias> alias = new ArrayList<>();

    @Version
    private Integer versjon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbPerson dbPerson = (DbPerson) o;

        if (getId() != null ? !getId().equals(dbPerson.getId()) : dbPerson.getId() != null) return false;
        if (getSistOppdatert() != null ? !getSistOppdatert().equals(dbPerson.getSistOppdatert()) : dbPerson.getSistOppdatert() != null)
            return false;
        if (getIdent() != null ? !getIdent().equals(dbPerson.getIdent()) : dbPerson.getIdent() != null) return false;
        if (getFornavn() != null ? !getFornavn().equals(dbPerson.getFornavn()) : dbPerson.getFornavn() != null)
            return false;
        if (getMellomnavn() != null ? !getMellomnavn().equals(dbPerson.getMellomnavn()) : dbPerson.getMellomnavn() != null)
            return false;
        if (getEtternavn() != null ? !getEtternavn().equals(dbPerson.getEtternavn()) : dbPerson.getEtternavn() != null)
            return false;
        if (getPerson() != null ? !getPerson().equals(dbPerson.getPerson()) : dbPerson.getPerson() != null)
            return false;
        return getRelasjoner() != null ? getRelasjoner().equals(dbPerson.getRelasjoner()) : dbPerson.getRelasjoner() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getSistOppdatert() != null ? getSistOppdatert().hashCode() : 0);
        result = 31 * result + (getIdent() != null ? getIdent().hashCode() : 0);
        result = 31 * result + (getFornavn() != null ? getFornavn().hashCode() : 0);
        result = 31 * result + (getMellomnavn() != null ? getMellomnavn().hashCode() : 0);
        result = 31 * result + (getEtternavn() != null ? getEtternavn().hashCode() : 0);
        result = 31 * result + (getPerson() != null ? getPerson().hashCode() : 0);
        result = 31 * result + (getRelasjoner() != null ? getRelasjoner().hashCode() : 0);
        return result;
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
                ", relasjoner=" + relasjoner +
                '}';
    }
}