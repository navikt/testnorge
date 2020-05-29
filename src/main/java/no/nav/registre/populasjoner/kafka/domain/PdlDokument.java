package no.nav.registre.populasjoner.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import no.nav.registre.populasjoner.kafka.person.PersonIdenterDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdlDokument {

    private Object hentPerson;
    private PersonIdenterDto hentIdenter;
}
