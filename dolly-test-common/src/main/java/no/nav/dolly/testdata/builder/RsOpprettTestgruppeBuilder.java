package no.nav.dolly.testdata.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.resultSet.RsOpprettTestgruppe;

@Getter
@Setter
@Builder
public class RsOpprettTestgruppeBuilder {
	private String navn;
	private String hensikt;

	public RsOpprettTestgruppe convertToRealRsOpprettTestgruppe(){
		RsOpprettTestgruppe testgruppe = new RsOpprettTestgruppe();
		testgruppe.setNavn(this.navn);
		testgruppe.setHensikt(this.hensikt);

		return testgruppe;
	}
}
