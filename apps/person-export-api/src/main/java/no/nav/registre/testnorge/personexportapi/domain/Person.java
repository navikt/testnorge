package no.nav.registre.testnorge.personexportapi.domain;

import no.nav.registre.testnorge.personexportapi.consumer.dto.EndringsmeldingDTO;

public class Person {
    private final EndringsmeldingDTO endringsmeldingDTO;

    public Person(EndringsmeldingDTO endringsmeldingDTO) {
        this.endringsmeldingDTO = endringsmeldingDTO;
    }

    public String getIdent() {
        return endringsmeldingDTO.getFodselsdato() + endringsmeldingDTO.getPersonnummer();
    }

    public String getFornavn() {
        return endringsmeldingDTO.getFornavn();
    }

    public String getMellomnavn() {
        return endringsmeldingDTO.getMellomnavn();
    }

    public String getEtternavn() {
        return endringsmeldingDTO.getSlektsnavn();
    }

}
