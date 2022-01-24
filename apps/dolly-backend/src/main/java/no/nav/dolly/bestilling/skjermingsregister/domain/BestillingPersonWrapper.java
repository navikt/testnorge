package no.nav.dolly.bestilling.skjermingsregister.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingPersonWrapper {

    private Person person;
    private PersonDTO pdlfPerson;
    private LocalDateTime skjermetFra;
    private LocalDateTime skjermetTil;
}
