package no.nav.registre.core.provider.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.core.database.model.Person;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonControllerResponse {
	private Person person;
	private String exceptionReason;

	public PersonControllerResponse(Person person) {
		this.person = person;
	}

	public PersonControllerResponse(String exceptionReason) {
		this.exceptionReason = exceptionReason;
	}
}
