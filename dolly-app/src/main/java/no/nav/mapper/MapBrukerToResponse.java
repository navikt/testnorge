package no.nav.mapper;

import no.nav.api.response.BrukerResponse;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Bruker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
@Component
public class MapBrukerToResponse {
	
	private MapTeamToResponse mapTeamToResponse;
	
	@Autowired
	public MapBrukerToResponse(MapTeamToResponse mapTeamToResponse) {
		this.mapTeamToResponse = mapTeamToResponse;
	}
	
	public BrukerResponse map(Bruker bruker) {
		BrukerResponse brukerResponse= new BrukerResponse(bruker.getNavIdent());
		if (bruker.getTeamEierskap() != null && !bruker.getTeamEierskap().isEmpty()) {
			Set<TeamResponse> teameierskap= bruker.getTeamEierskap().stream().map(mapTeamToResponse::map).collect(Collectors.toSet());
			brukerResponse.setTeamEierskap(teameierskap);
		}
		if (bruker.getTeamMedlemskap() != null && !bruker.getTeamMedlemskap().isEmpty()) {
			Set<TeamResponse> teamMedlemskap= bruker.getTeamMedlemskap().stream().map(mapTeamToResponse::map).collect(Collectors.toSet());
			brukerResponse.setTeamMedlemskap(teamMedlemskap);
		}
		return brukerResponse;
	}
}
