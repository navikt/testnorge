package no.nav.dolly.domain.jpa;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "T_TEAM")
@Builder
public class Team {

    @Id
    @GeneratedValue(generator = "teamIdGenerator")
    @GenericGenerator(name = "teamIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_TEAM_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(nullable = false, unique = true)
    private String navn;

    private String beskrivelse;

    @Column(name = "DATO_OPPRETTET", nullable = false)
    private LocalDate datoOpprettet;

    @ManyToOne
    @JoinColumn(name = "EIER", nullable = false)
    private Bruker eier;

    @OneToMany(mappedBy = "teamtilhoerighet", fetch = FetchType.LAZY)
    private List<Testgruppe> grupper;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "T_TEAM_MEDLEMMER",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "bruker_id"))
    private List<Bruker> medlemmer;

    public Team(String navn, Bruker eier) {
        this.navn = navn;
        this.eier = eier;
        this.datoOpprettet = LocalDate.now();
    }

    public List<Testgruppe> getGrupper() {
        if (grupper == null) {
            grupper = new ArrayList<>();
        }
        return grupper;
    }

    public List<Bruker> getMedlemmer() {
        if (medlemmer == null) {
            medlemmer = new ArrayList<>();
        }
        return medlemmer;
    }
}
