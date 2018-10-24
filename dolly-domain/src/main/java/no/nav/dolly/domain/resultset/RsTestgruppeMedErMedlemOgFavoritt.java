package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
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
	private Set<RsTestidentBestillingId> testidenter = getTestidenter();

	private boolean erMedlemAvTeamSomEierGruppe;
	private boolean favorittIGruppen;

	private Set<RsTestidentBestillingId> getTestidenter(){
		if (testidenter == null){
			testidenter = new HashSet<>();
		}
		return testidenter;
	}


}
