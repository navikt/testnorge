package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.dolly.domain.resultset.Tags;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GRUPPE")
public class Testgruppe implements Serializable {

    @Id
    @GeneratedValue(generator = "gruppeIdGenerator")
    @GenericGenerator(name = "gruppeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "GRUPPE_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Version
    @Column(name = "VERSJON")
    private Long versjon;

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

    @OneToMany(mappedBy = "testgruppe", fetch = FetchType.EAGER)
    @Column(unique = true)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @ToString.Exclude
    @OrderBy("id DESC")
    private List<Testident> testidenter = new ArrayList<>();

    @ManyToMany(mappedBy = "favoritter", fetch = FetchType.EAGER)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Bruker> favorisertAv = new HashSet<>();

    @OrderBy("id")
    @OneToMany(mappedBy = "gruppe", fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Bestilling> bestillinger = new HashSet<>();

    @Column(name = "ER_LAAST")
    private Boolean erLaast;

    @Column(name = "LAAST_BESKRIVELSE")
    private String laastBeskrivelse;

    @Column(name = "TAGS")
    private String tags;

    public List<Tags> getTags() {

        return isBlank(tags) ?
                new ArrayList<>() :
                new ArrayList<>(Arrays.stream(tags.split(","))
                        .map(Tags::valueOf)
                        .toList());
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

