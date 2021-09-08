package no.nav.testnav.apps.importpersonservice.consumer.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.testnav.apps.importpersonservice.consumer.dto.PersonDTO;
import no.nav.testnav.apps.importpersonservice.domain.Person;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OppdaterPersonRequest {
    PersonDTO person;

    public OppdaterPersonRequest(Person person){
        this.person = new PersonDTO(person);
    }
}
