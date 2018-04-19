package no.nav.mapper;

import no.nav.api.response.BrukerResponse;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Bruker;

import java.util.Set;
import java.util.stream.Collectors;

public class MapBrukerToResponse {
	public static BrukerResponse map(Bruker bruker) {
		BrukerResponse brukerResponse= new BrukerResponse(bruker.getNavIdent());
		if (bruker.getTeamEierskap() != null && !bruker.getTeamEierskap().isEmpty()) {
			Set<TeamResponse> teameierskap= bruker.getTeamEierskap().stream().map(MapTeamToResponse::map).collect(Collectors.toSet());
			brukerResponse.setTeamEierskap(teameierskap);
		}
		if (bruker.getTeamMedlemskap() != null && !bruker.getTeamMedlemskap().isEmpty()) {
			Set<TeamResponse> teamMedlemskap= bruker.getTeamMedlemskap().stream().map(MapTeamToResponse::map).collect(Collectors.toSet());
			brukerResponse.setTeamMedlemskap(teamMedlemskap);
		}
		return brukerResponse;
	}
}
