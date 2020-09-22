package no.nav.dolly.domain.jpa;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_BRUKER")
public class Bruker {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(generator = "brukerIdGenerator")
    @GenericGenerator(name = "brukerIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_BRUKER_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "BRUKER_ID")
    private String brukerId;

    @EqualsAndHashCode.Exclude
    @Column(name = "BRUKERNAVN")
    private String brukernavn;

    @EqualsAndHashCode.Exclude
    @Column(name ="EPOST")
    private String epost;

    @EqualsAndHashCode.Exclude
    @Column(name = "NAV_IDENT", length = 10)
    private String navIdent;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "opprettetAv")
    private Set<Testgruppe> testgrupper;

    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(name = "T_BRUKER_FAVORITTER",
            joinColumns = @JoinColumn(name = "bruker_id"),
            inverseJoinColumns = @JoinColumn(name = "gruppe_id"))
    private Set<Testgruppe> favoritter;

    public Set<Testgruppe> getFavoritter() {
        if (isNull(favoritter)) {
            favoritter = new HashSet();
        }
        return favoritter;
    }

    public Set<Testgruppe> getTestgrupper() {
        if (isNull(testgrupper)) {
            testgrupper = new HashSet();
        }
        return testgrupper;
    }
}
