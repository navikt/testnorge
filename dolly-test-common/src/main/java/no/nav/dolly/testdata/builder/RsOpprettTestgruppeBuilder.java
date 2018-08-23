package no.nav.dolly.testdata.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultSet.RsOpprettTestgruppe;

@Getter
@Setter
@Builder
public class RsOpprettTestgruppeBuilder {
	private String navn;
	private String hensikt;
	private Long teamId;

	public RsOpprettTestgruppe convertToRealRsOpprettTestgruppe(){
		RsOpprettTestgruppe testgruppe = new RsOpprettTestgruppe();
		testgruppe.setNavn(this.navn);
		testgruppe.setHensikt(this.hensikt);
		testgruppe.setTeamId(this.teamId);

		return testgruppe;
	}
}
