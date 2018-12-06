package no.nav.dolly.domain.resultset;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsTestgruppeUtvidet extends RsTestgruppe{

	private List<RsBestilling> bestillinger;

	private List<RsTestidentBestillingId> testidenter;

	public List<RsBestilling> getBestillinger() {
		if (bestillinger == null) {
			bestillinger = new ArrayList<>();
		}
		return bestillinger;
	}

	public List<RsTestidentBestillingId> getTestidenter() {
		if (testidenter == null) {
			testidenter = new ArrayList<>();
		}
		return testidenter;
	}
}
