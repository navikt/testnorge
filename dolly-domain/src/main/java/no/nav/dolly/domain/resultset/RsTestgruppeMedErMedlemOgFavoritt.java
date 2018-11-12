package no.nav.dolly.domain.resultset;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsTestgruppeMedErMedlemOgFavoritt {
	private Long id;
	private String navn;
	private String hensikt;
	private String opprettetAvNavIdent;
	private String sistEndretAvNavIdent;
	private String openAmSent;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate datoEndret;

	private List<RsBestilling> bestillinger = new ArrayList<>();

	private RsTeamMedIdOgNavn team;
	private Set<RsTestidentBestillingId> testidenter = new HashSet<>();

	private boolean erMedlemAvTeamSomEierGruppe;
	private boolean favorittIGruppen;


}
