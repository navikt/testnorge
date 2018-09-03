package no.nav.dolly.testdata.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.domain.resultset.RsTestident;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
public class RsTestgruppeBuilder {
	private Long id;
	private String navn;
	private String opprettetAvNavIdent;
	private String sistEndretAvNavIdent;
	private LocalDate datoEndret;
	private Set<RsTestident> testidenter;

	public RsTestgruppe convertToRealRsTestgruppe(){
		RsTestgruppe testgruppe = new RsTestgruppe();
		testgruppe.setDatoEndret(this.datoEndret);
		testgruppe.setId(this.id);
		testgruppe.setSistEndretAvNavIdent(this.opprettetAvNavIdent);
		testgruppe.setOpprettetAvNavIdent(this.sistEndretAvNavIdent);
		testgruppe.setTestidenter(this.testidenter);
		testgruppe.setNavn(this.navn);

		return testgruppe;
	}
}
