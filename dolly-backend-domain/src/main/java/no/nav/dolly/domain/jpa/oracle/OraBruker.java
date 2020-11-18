package no.nav.dolly.domain.jpa.oracle;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.postgres.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_BRUKER")
public class OraBruker {

    @Id
    @GeneratedValue(generator = "oraBrukerIdGenerator")
    @GenericGenerator(name = "oraBrukerIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_BRUKER_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "BRUKER_ID")
    private String brukerId;

    @Column(name = "BRUKERNAVN")
    private String brukernavn;

    @Column(name = "EPOST")
    private String epost;

    @Column(name = "NAV_IDENT", length = 10)
    private String navIdent;

    @Column(name = "MIGRERT")
    private Boolean migrert;

    @ManyToOne
    @JoinColumn(name = "EID_AV_ID")
    private OraBruker eidAv;

    @OneToMany(mappedBy = "opprettetAv")
    private Set<OraTestgruppe> testgrupper;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "T_BRUKER_FAVORITTER",
            joinColumns = @JoinColumn(name = "bruker_id"),
            inverseJoinColumns = @JoinColumn(name = "gruppe_id"))
    private Set<OraTestgruppe> favoritter;

    public Set<OraTestgruppe> getFavoritter() {
        if (isNull(favoritter)) {
            favoritter = new HashSet<>();
        }
        return favoritter;
    }

    public Set<OraTestgruppe> getTestgrupper() {
        if (isNull(testgrupper)) {
            testgrupper = new HashSet<>();
        }
        return testgrupper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof OraBruker))
            return false;

        OraBruker bruker = (OraBruker) o;

        return new EqualsBuilder()
                .append(getId(), bruker.getId())
                .append(getBrukerId(), bruker.getBrukerId())
                .append(getBrukernavn(), bruker.getBrukernavn())
                .append(getEpost(), bruker.getEpost())
                .append(getNavIdent(), bruker.getNavIdent())
                .append(getMigrert(), bruker.getMigrert())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getBrukerId())
                .append(getBrukernavn())
                .append(getEpost())
                .append(getNavIdent())
                .append(getMigrert())
                .toHashCode();
    }
}
