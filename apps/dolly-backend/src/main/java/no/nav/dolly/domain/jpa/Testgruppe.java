package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.Tags;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GRUPPE")
public class Testgruppe {

    @Id
    @GeneratedValue(generator = "gruppeIdGenerator")
    @GenericGenerator(name = "gruppeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "GRUPPE_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(nullable = false)
    private String navn;

    @Column
    private String hensikt;

    @ManyToOne
    @JoinColumn(name = "OPPRETTET_AV", nullable = false)
    private Bruker opprettetAv;

    @ManyToOne
    @JoinColumn(name = "SIST_ENDRET_AV", nullable = false)
    private Bruker sistEndretAv;

    @Column(name = "DATO_ENDRET", nullable = false)
    private LocalDate datoEndret;

    @OneToMany(mappedBy = "testgruppe", fetch = FetchType.LAZY)
    @Column(unique = true)
    private List<Testident> testidenter;

    @ManyToMany(mappedBy = "favoritter", fetch = FetchType.LAZY)
    private Set<Bruker> favorisertAv;

    @OrderBy("id")
    @OneToMany(mappedBy = "gruppe", fetch = FetchType.LAZY)
    private Set<Bestilling> bestillinger;

    @Column(name = "ER_LAAST")
    private Boolean erLaast;

    @Column(name = "LAAST_BESKRIVELSE")
    private String laastBeskrivelse;

    @Column(name = "TAGS")
    private String tags;

    public List<Testident> getTestidenter() {
        if (isNull(testidenter)) {
            testidenter = new ArrayList<>();
        }
        return testidenter;
    }

    public Set<Bruker> getFavorisertAv() {
        if (isNull(favorisertAv)) {
            favorisertAv = new HashSet<>();
        }
        return favorisertAv;
    }

    public List<Tags> getTags() {
        if (isNull(tags)) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(",")).map(Tags::valueOf).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Testgruppe that))
            return false;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getNavn(), that.getNavn())
                .append(getHensikt(), that.getHensikt())
                .append(getOpprettetAv(), that.getOpprettetAv())
                .append(getSistEndretAv(), that.getSistEndretAv())
                .append(getDatoEndret(), that.getDatoEndret())
                .append(getErLaast(), that.getErLaast())
                .append(getLaastBeskrivelse(), that.getLaastBeskrivelse())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getNavn())
                .append(getHensikt())
                .append(getOpprettetAv())
                .append(getSistEndretAv())
                .append(getDatoEndret())
                .append(getErLaast())
                .append(getLaastBeskrivelse())
                .toHashCode();
    }
}

