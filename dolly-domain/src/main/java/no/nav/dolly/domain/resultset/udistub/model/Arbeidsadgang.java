package no.nav.dolly.domain.resultset.udistub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Arbeidsadgang {

	private String harArbeidsAdgang;

	private String typeArbeidsadgang;
	private String arbeidsOmfang;
	private Periode periode;
	private Person person;
}
