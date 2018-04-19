package no.nav.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Team;

import java.util.stream.Collectors;

public class TeamCustomMapper extends CustomMapper<Team, TeamResponse> {
	
	@Override
	public void mapAtoB(Team a, TeamResponse b, MappingContext context) {
		b.setBrukernesNavIdent(a.getSetOfBrukernesNavidenter());
		
		if(a.getGrupper()!=null && !a.getGrupper().isEmpty()) {
			b.setGrupper(a.getGrupper()
					.stream()
					.map(testgruppe -> MapTestgruppeToResponse.map(testgruppe))
					.collect(Collectors.toSet()));
		}
	}
	
	@Override
	public void mapBtoA(TeamResponse b, Team a, MappingContext context) {
	}
	
}

