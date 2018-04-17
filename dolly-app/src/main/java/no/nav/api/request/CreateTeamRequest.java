package no.nav.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Jarl Øystein Samseth, Visma Consulting
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeamRequest {
	
	private String navn;
	
	private String beskrivelse;
	
	private String eierensNavIdent;
}
