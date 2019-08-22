package no.nav.dolly.domain.resultset.udistub;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.Alias;
import no.nav.dolly.domain.resultset.udistub.model.Arbeidsadgang;
import no.nav.dolly.domain.resultset.udistub.model.Avgjoerelse;
import no.nav.dolly.domain.resultset.udistub.model.MangelfullDato;
import no.nav.dolly.domain.resultset.udistub.model.PersonNavn;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdStatus;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RsUdiPersonData {

		private List<Alias> aliaser;
		private Arbeidsadgang arbeidsadgang;
		private Boolean avgjoerelseUavklart;
		private List<Avgjoerelse> avgjoerelser;
		private Boolean flyktning;
		private String fnr;
		private MangelfullDato foedselsDato;
		private String id;
		private PersonNavn navn;
		private OppholdStatus oppholdsStatus;
		private Boolean oppholdstilatelse;
		private String soeksnadOmBeskyttelseUnderBehandling;
		private Date soknadDato;
}
