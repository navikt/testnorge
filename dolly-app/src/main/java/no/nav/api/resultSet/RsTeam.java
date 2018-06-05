package no.nav.api.resultSet;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class RsTeam {
	private Long id;
	private String navn;
	private String beskrivelse;
	private LocalDateTime datoOpprettet;
	private RsBruker eier;
	private Set<RsBruker> medlemmer;
	private Set<RsTestgruppe> grupper;
	
}
