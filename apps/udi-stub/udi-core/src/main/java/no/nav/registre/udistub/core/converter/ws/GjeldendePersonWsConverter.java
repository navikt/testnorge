package no.nav.registre.udistub.core.converter.ws;


import no.udi.mt_1067_nav_data.v1.Alias;
import no.udi.mt_1067_nav_data.v1.AliasListe;
import no.udi.mt_1067_nav_data.v1.GjeldendePerson;
import no.udi.mt_1067_nav_data.v1.MangelfullDato;
import no.udi.mt_1067_nav_data.v1.PersonNavn;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import no.nav.registre.udistub.core.service.to.UdiAlias;
import no.nav.registre.udistub.core.service.to.UdiPerson;

@Component
public class GjeldendePersonWsConverter implements Converter<UdiPerson, GjeldendePerson> {
    @Override
    public GjeldendePerson convert(UdiPerson person) {
        GjeldendePerson gjeldendePerson = new GjeldendePerson();
        gjeldendePerson.setFodselsnummer(person.getIdent());

        if (person.getFoedselsDato() != null) {
            MangelfullDato mangelfullDato = new MangelfullDato();
            mangelfullDato.setDag(person.getFoedselsDato().getDayOfMonth());
            mangelfullDato.setManed(person.getFoedselsDato().getMonthValue());
            mangelfullDato.setAr(person.getFoedselsDato().getYear());
            gjeldendePerson.setFodselsdato(mangelfullDato);
        }
        if (!person.getAliaser().isEmpty()) {
            gjeldendePerson.setAlias(new AliasListe());
            person.getAliaser().stream().filter(this::filterAlias).map(a -> {
                Alias alias = new Alias();
                PersonNavn navn = new PersonNavn();
                navn.setFornavn(a.getNavn().getFornavn());
                navn.setMellomnavn(a.getNavn().getMellomnavn());
                navn.setEtternavn(a.getNavn().getEtternavn());
                alias.setNavn(navn);

                alias.setFodselsnummer(a.getFnr());
                return alias;
            }).forEach(a -> gjeldendePerson.getAlias().getAlias().add(a));
        }


        if (person.getNavn() != null) {
            PersonNavn navn = new PersonNavn();
            navn.setFornavn(person.getNavn().getFornavn());
            navn.setMellomnavn(person.getNavn().getMellomnavn());
            navn.setEtternavn(person.getNavn().getEtternavn());
            gjeldendePerson.setNavn(navn);
        }

        return gjeldendePerson;
    }

    private boolean filterAlias(UdiAlias a) {
        return a.getNavn() != null && (a.getFnr() != null && !a.getFnr().isEmpty());
    }
}
