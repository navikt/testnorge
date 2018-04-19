package no.nav.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BrukerResponse {
	private String navIdent;
	private Set<TeamResponse> teamMedlemskap;
	private Set<TeamResponse> teamEierskap;
	
	public BrukerResponse(String navIdent) {
		this.navIdent = navIdent;
	}
}
