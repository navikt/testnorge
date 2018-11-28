package no.nav.dolly.domain.resultset;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsTestgruppeUtvidet extends RsTestgruppe{

	private List<RsBestilling> bestillinger = new ArrayList<>();

	private List<RsTestidentBestillingId> testidenter = new ArrayList<>();

	private boolean erMedlemAvTeamSomEierGruppe;
	private boolean favorittIGruppen;
}
