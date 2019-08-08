package no.nav.registre.core.converter.udi;

import no.nav.registre.core.converter.BaseConverter;
import no.nav.registre.core.database.model.Person;
import no.udi.mt_1067_nav_data.v1.GjeldendePerson;
import no.udi.mt_1067_nav_data.v1.PersonNavn;
import org.springframework.stereotype.Component;

@Component
public class GjeldenePersonConverter extends BaseConverter<Person, GjeldendePerson> {

    @Override
    public GjeldendePerson convert(Person person) {
        GjeldendePerson gjeldendePerson = new GjeldendePerson();
        gjeldendePerson.setFodselsnummer(person.getFnr());
        gjeldendePerson.setNavn(getNavn(person));
        return gjeldendePerson;
    }

    private PersonNavn getNavn(Person person) {
        no.udi.mt_1067_nav_data.v1.PersonNavn personNavn = new no.udi.mt_1067_nav_data.v1.PersonNavn();
        personNavn.setFornavn(person.getNavn().getFornavn());
        personNavn.setEtternavn(person.getNavn().getEtternavn());
        personNavn.setMellomnavn(person.getNavn().getMellomnavn());
        return personNavn;
    }
}