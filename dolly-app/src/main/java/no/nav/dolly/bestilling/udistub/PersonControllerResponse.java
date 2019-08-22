package no.nav.dolly.bestilling.udistub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.Person;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonControllerResponse {
	private Person person;
	private String reason;

	public PersonControllerResponse(Person person) {
		this.person = person;
	}

	public PersonControllerResponse(String reason) {
		this.reason = reason;
	}
}
