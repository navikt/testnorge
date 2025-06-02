package no.nav.dolly.domain.jpa;

import jakarta.persistence.CascadeType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tilhoerer_gruppe")
    @Column(unique = true)
    @Builder.Default
    @OrderBy("id DESC")
    private List<Testident> testidenter = new ArrayList<>();

    @ManyToMany(mappedBy = "favoritter", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Bruker> favorisertAv = new HashSet<>();

    @OrderBy("id")
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "gruppe_id")
    @Builder.Default
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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Testgruppe that = (Testgruppe) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(versjon, that.versjon)
                .append(navn, that.navn)
                .append(hensikt, that.hensikt)
                .append(opprettetAv, that.opprettetAv)
                .append(sistEndretAv, that.sistEndretAv)
                .append(datoEndret, that.datoEndret)
                .append(erLaast, that.erLaast)
                .append(laastBeskrivelse, that.laastBeskrivelse)
                .append(tags, that.tags)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(versjon)
                .append(navn)
                .append(hensikt)
                .append(opprettetAv)
                .append(sistEndretAv)
                .append(datoEndret)
                .append(erLaast)
                .append(laastBeskrivelse)
                .append(tags)
                .toHashCode();
    }
}

