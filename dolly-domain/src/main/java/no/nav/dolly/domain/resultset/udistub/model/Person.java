package no.nav.dolly.domain.resultset.udistub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.opphold.OppholdStatus;

import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Person {

	private String fnr;
	private PersonNavn navn;
	private MangelfullDato foedselsDato;

	private List<Alias> aliaser;
	private List<Avgjoerelse> avgjoerelser;
	private Arbeidsadgang arbeidsadgang;
	private OppholdStatus oppholdStatus;

	private boolean avgjoerelseUavklart;

	private Boolean oppholdsTilatelse;
	private Boolean flyktning;

	private String soeknadOmBeskyttelseUnderBehandling;
	private Date soknadDato;
}
