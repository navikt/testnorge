package no.nav.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.nav.jpa.Bruker;

import java.time.LocalDateTime;

/**
 * @author Jarl Øystein Samseth, Visma Consulting
 */
@Getter
@AllArgsConstructor
public class CreateTeamRequest {
	
	private String navn;
	
	private String beskrivelse;
	
	private String eierensNavIdent;
}
