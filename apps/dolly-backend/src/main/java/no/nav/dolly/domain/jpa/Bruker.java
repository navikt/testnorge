package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BRUKER")
public class Bruker {

    @Id
    @GeneratedValue(generator = "brukerIdGenerator")
    @GenericGenerator(name = "brukerIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "BRUKER_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "BRUKER_ID")
    private String brukerId;

    @Column(name = "BRUKERNAVN")
    private String brukernavn;

    @Column(name = "BANKID_BRUKERNAVN")
    private String bankidBrukernavn;

    @Column(name = "BANKID_TOKEN")
    private String bankidToken;

    @Column(name = "EPOST")
    private String epost;

    @Column(name = "NAV_IDENT", length = 10)
    private String navIdent;

    @Column(name = "MIGRERT")
    private Boolean migrert;

    @ManyToOne
    @JoinColumn(name = "EID_AV_ID")
    private Bruker eidAv;

    @OneToMany(mappedBy = "opprettetAv")
    private Set<Testgruppe> testgrupper;

    @ManyToMany
    @JoinTable(name = "BRUKER_FAVORITTER",
            joinColumns = @JoinColumn(name = "bruker_id"),
            inverseJoinColumns = @JoinColumn(name = "gruppe_id"))
    private Set<Testgruppe> favoritter;

    public Set<Testgruppe> getFavoritter() {
        if (isNull(favoritter)) {
            favoritter = new HashSet<>();
        }
        return favoritter;
    }

    public Set<Testgruppe> getTestgrupper() {
        if (isNull(testgrupper)) {
            testgrupper = new HashSet<>();
        }
        return testgrupper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Bruker bruker))
            return false;

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
