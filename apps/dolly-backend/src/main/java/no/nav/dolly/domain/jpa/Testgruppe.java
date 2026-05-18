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
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("gruppe")
public class Testgruppe implements Serializable {

    @Id
    private Long id;

    @Version
    @Column("versjon")
    private Long versjon;

    @Column("navn")
    private String navn;

    @Column("hensikt")
    private String hensikt;

    @Column("opprettet_av")
    private Long opprettetAvId;

    @Column("sist_endret_av")
    private Long sistEndretAvId;

    @Transient
    private Bruker opprettetAv;

    @Transient
    private Bruker sistEndretAv;

    @Column("dato_endret")
    private LocalDate datoEndret;

    @Column("er_laast")
    private Boolean erLaast;

    @Column("laast_beskrivelse")
    private String laastBeskrivelse;

    @Column("tags")
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

