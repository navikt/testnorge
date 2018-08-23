package no.nav.dolly.testdata.builder;

import lombok.Builder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;

import java.time.LocalDate;
import java.util.Set;

@Builder
public class TeamBuilder {

    private Long id;
    private String navn;
    private String beskrivelse;
    private LocalDate datoOpprettet;
    private Bruker eier;
    private Set<Testgruppe> grupper;
    private Set<Bruker> medlemmer;

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
