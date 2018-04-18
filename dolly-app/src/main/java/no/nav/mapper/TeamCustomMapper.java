package no.nav.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Team;

import java.util.HashSet;
import java.util.Set;

public class TeamCustomMapper extends CustomMapper<Team, TeamResponse> {
	
	@Override
	public void mapAtoB(Team a, TeamResponse b, MappingContext context) {
		b.setEierensNavIdent(a.getEier().getNavIdent());
		Set<String> brukernesNavIdent = new HashSet<>();
		a.getBrukere().forEach(bruker -> brukernesNavIdent.add(bruker.getNavIdent()));
		b.setBrukernesNavIdent(brukernesNavIdent);
		
		//TODO kalle mapperen til Testgruppe: MapTestgruppeToResponse.map(a.getTestgruppe());
	}
	
	@Override
	public void mapBtoA(TeamResponse b, Team a, MappingContext context) {
	}
	
}

