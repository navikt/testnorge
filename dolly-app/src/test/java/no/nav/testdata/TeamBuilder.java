package no.nav.testdata;

import lombok.Builder;
import lombok.Data;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
public class TeamBuilder {

    private Long id;
    private String navn;
    private String beskrivelse;
    private LocalDateTime datoOpprettet;
    private Bruker eier;
    private Set<Testgruppe> grupper = new HashSet<>();
    private Set<Bruker> medlemmer = new HashSet<>();

    public Team convertToRealTeam(){
        Team team = new Team();
        team.setId(this.id);
        team.setNavn(this.navn);
        team.setBeskrivelse(this.beskrivelse);
        team.setDatoOpprettet(this.datoOpprettet);
        team.setEier(this.eier);
        team.setGrupper(this.grupper);
        team.setMedlemmer(this.medlemmer);

        return team;
    }
}
