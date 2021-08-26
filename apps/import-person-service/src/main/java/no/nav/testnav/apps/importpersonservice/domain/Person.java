package no.nav.testnav.apps.importpersonservice.domain;

import no.nav.testnav.apps.importpersonservice.controller.dto.PersonDTO;

public class Person {
    private final String ident;

    public Person(String ident){
        this.ident = ident;
    }

    public Person(PersonDTO dto) {
        this.ident = dto.getIdent();
    }

    public String getIdent() {
        return ident;
    }
}
