package no.nav.dolly.testdata.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.resultSet.RsBruker;
import no.nav.resultSet.RsTestgruppe;

import java.util.Set;

@Setter
@Getter
@Builder
public class RsBrukerBuilder {
	private String navIdent;
	private Set<RsTestgruppe> favoritter;

	public RsBruker convertToRealRsBruker(){
		RsBruker bruker = new RsBruker();
		bruker.setNavIdent(this.navIdent);
		bruker.setFavoritter(this.favoritter);

		return bruker;
	}

}
