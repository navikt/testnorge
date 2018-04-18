package no.nav.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {
	private Long id;
	private String navn;
	private String beskrivelse;
	private LocalDateTime datoOpprettet;
	private String eierensNavIdent;
	private Set<String> brukernesNavIdent;
	private Set<TestgruppeResponse> grupper;
	
}
