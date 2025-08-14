package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BRUKER")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Bruker implements Serializable {

    @Id
    @GeneratedValue(generator = "brukerIdGenerator")
    @GenericGenerator(name = "brukerIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "BRUKER_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Version
    @Column(name = "VERSJON")
    private Long versjon;

    @Column(name = "BRUKER_ID", unique = true)
    private String brukerId;

    @Column(name = "BRUKERNAVN")
    private String brukernavn;

    @Column(name = "EPOST")
    private String epost;

    @Column(name = "BRUKERTYPE")
    @Enumerated(EnumType.STRING)
    private Brukertype brukertype;

    @Transient
    @Builder.Default
    private List<String> grupper = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "opprettet_av")
    @Builder.Default
    private Set<Testgruppe> testgrupper = new HashSet<>();

    @ManyToMany
    @Builder.Default
    @JoinTable(name = "BRUKER_FAVORITTER",
            joinColumns = @JoinColumn(name = "bruker_id"),
            inverseJoinColumns = @JoinColumn(name = "gruppe_id"))
    private Set<Testgruppe> favoritter = new HashSet<>();


    @ManyToMany
    @Builder.Default
    @JoinTable(name = "TEAM_BRUKER",
            joinColumns = @JoinColumn(name = "bruker_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<Team> teamMedlemskap = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPRESENTERER_TEAM")
    private Team representererTeam;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Bruker bruker = (Bruker) o;

        return new EqualsBuilder()
                .append(id, bruker.id)
                .append(versjon, bruker.versjon)
                .append(brukerId, bruker.brukerId)
                .append(brukernavn, bruker.brukernavn)
                .append(epost, bruker.epost)
                .append(brukertype, bruker.brukertype)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(versjon)
                .append(brukerId)
                .append(brukernavn)
                .append(epost)
                .append(brukertype)
                .toHashCode();
    }

    public enum Brukertype {AZURE, BANKID, TEAM}
}
