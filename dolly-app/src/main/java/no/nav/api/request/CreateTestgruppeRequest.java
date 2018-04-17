package no.nav.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTestgruppeRequest {
	private String navn;
	private String opprettetAvNavIdent;
	private Long tilhoererTeamId;
}
