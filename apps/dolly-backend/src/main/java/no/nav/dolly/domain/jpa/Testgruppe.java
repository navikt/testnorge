package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.Tags;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("GRUPPE")
public class Testgruppe implements Serializable {

    @Id
    private Long id;

    @Version
    @Column("VERSJON")
    private Long versjon;

    @Column("NAVN")
    private String navn;

    @Column("HENSIKT")
    private String hensikt;

//    @ManyToOne
//    @JoinColumn("OPPRETTET_AV", nullable = false)
    @Column("OPPRETTET_AV")
    private Long opprettetAvId;

    @Transient
    private Bruker opprettetAv;

//    @ManyToOne
//    @JoinColumn("SIST_ENDRET_AV", nullable = false)
    @Column("SIST_ENDRET_AV")
    private Long sistEndretAvId;

    @Transient
    private Bruker sistEndretAv;

    @Column("DATO_ENDRET")
    private LocalDate datoEndret;

//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn("tilhoerer_gruppe")
//    @Column(unique = true)
    @Builder.Default
//    @OrderBy("id DESC")
    private List<Testident> testidenter = new ArrayList<>();

//    @ManyToMany(mappedBy = "favoritter", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<Bruker> favorisertAv = new HashSet<>();

//    @OrderBy("id")
//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn("gruppe_id")
    @Builder.Default
    private Set<Bestilling> bestillinger = new HashSet<>();

    @Column("ER_LAAST")
    private Boolean erLaast;

    @Column("LAAST_BESKRIVELSE")
    private String laastBeskrivelse;

    @Column("TAGS")
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

