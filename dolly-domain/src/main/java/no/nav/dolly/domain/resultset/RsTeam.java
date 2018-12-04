package no.nav.dolly.domain.resultset;

import java.time.LocalDate;
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
	private Integer antallMedlemmer;
}