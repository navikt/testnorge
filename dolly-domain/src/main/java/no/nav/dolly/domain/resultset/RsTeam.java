package no.nav.dolly.domain.resultset;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RsTeam {
	private Long id;
	private String navn;
	private String beskrivelse;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate datoOpprettet;

	private String eierNavIdent;
	private Set<RsTestgruppe> grupper;
	private Set<RsBruker> medlemmer;

	public Set<RsTestgruppe> getGrupper() {
		if (grupper == null) {
			grupper = new HashSet<>();
		}
		return grupper;
	}

	public Set<RsBruker> getMedlemmer() {
		if (medlemmer == null) {
			medlemmer = new HashSet<>();
		}
		return medlemmer;
	}
}
