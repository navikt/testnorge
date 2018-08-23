package no.nav.dolly.testdata.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultSet.RsBruker;
import no.nav.dolly.domain.resultSet.RsTeam;
import no.nav.dolly.domain.resultSet.RsTestgruppe;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Builder
public class RsTeamBuilder {
	private Long id;
	private String navn;
	private String beskrivelse;
	private LocalDate datoOpprettet;
	private String eierNavIdent;
	private Set<RsBruker> medlemmer;
	private Set<RsTestgruppe> grupper;

	public RsTeam convertToRealRsTeam(){
		RsTeam team = new RsTeam();
		team.setId(this.id);
		team.setNavn(this.navn);
		team.setBeskrivelse(this.beskrivelse);
		team.setDatoOpprettet(this.datoOpprettet);
		team.setEierNavIdent(this.eierNavIdent);
		team.setGrupper(this.grupper);
		team.setMedlemmer(this.medlemmer);

		return team;
	}

}
