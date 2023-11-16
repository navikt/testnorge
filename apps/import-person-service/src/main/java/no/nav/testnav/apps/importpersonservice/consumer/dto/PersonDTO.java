package no.nav.testnav.apps.importpersonservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.testnav.apps.importpersonservice.domain.Person;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO;

import java.util.Collections;
import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {
    String ident;
    List<NavnDTO> navn;

    public PersonDTO(Person person) {
        this.ident = person.getIdent();
        this.navn = Collections.singletonList(new NavnDTO(DbVersjonDTO.Master.FREG)); // TODO: Remove hack when pdl-forvalter is ready
    }
}
