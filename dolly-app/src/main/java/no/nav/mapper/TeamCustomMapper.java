package no.nav.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class TeamCustomMapper extends CustomMapper<Team, TeamResponse> {
	
	private MapTestgruppeToResponse mapTestgruppeToResponse;
	
	@Autowired
	public TeamCustomMapper(MapTestgruppeToResponse mapTestgruppeToResponse) {
		this.mapTestgruppeToResponse = mapTestgruppeToResponse;
	}
	
	@Override
	public void mapAtoB(Team a, TeamResponse b, MappingContext context) {
		b.setMedlemmenesNavIdent(a.getSetOfBrukernesNavidenter());
		
		if(a.getGrupper()!=null && !a.getGrupper().isEmpty()) {
			b.setGrupper(a.getGrupper()
					.stream()
					.map(testgruppe -> mapTestgruppeToResponse.map(testgruppe))
					.collect(Collectors.toSet()));
		}
	}
	
	@Override
	public void mapBtoA(TeamResponse b, Team a, MappingContext context) {
	}
	
}

