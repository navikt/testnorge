package no.nav.dolly.domain.resultSet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class RsTestgruppeMedErMedlemOgFavoritt {
	private Long id;
	private String navn;
	private String hensikt;
	private String opprettetAvNavIdent;
	private String sistEndretAvNavIdent;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate datoEndret;

	private List<RsBestilling> bestillinger = new ArrayList<>();

	private RsTeamMedIdOgNavn team;
	private Set<RsTestident> testidenter = new HashSet<>();

	private boolean erMedlemAvTeamSomEierGruppe;
	private boolean favorittIGruppen;


}
