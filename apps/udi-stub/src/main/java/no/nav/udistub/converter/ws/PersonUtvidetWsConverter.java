package no.nav.udistub.converter.ws;

import no.nav.udistub.service.dto.UdiPerson;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusResultat;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class PersonUtvidetWsConverter implements Converter<UdiPerson, HentUtvidetPersonstatusResultat> {

    @Override
    public HentUtvidetPersonstatusResultat convert(UdiPerson person) {

        if (nonNull(person)) {
            var xmlDateWsConverter = new XmlDateWsConverter();
            var gjeldendePersonWsConverter = new GjeldendePersonWsConverter();
            var arbeidsadgangUtvidetWsConverter = new ArbeidsadgangUtvidetWsConverter();
            var hentPersonstatusResultat = new HentUtvidetPersonstatusResultat();
            var beskyttleseUnderBehandlingWsConverter = new BeskyttleseUnderBehandlingWsConverter();
            var gjeldendeOppholdStatusWsConverter = new GjeldendeOppholdStatusWsConverter();

            hentPersonstatusResultat.setGjeldendePerson(gjeldendePersonWsConverter.convert(person));
            hentPersonstatusResultat.setArbeidsadgang(arbeidsadgangUtvidetWsConverter.convert(person.getArbeidsadgang()));
            hentPersonstatusResultat.setForesporselsfodselsnummer(person.getIdent());
            hentPersonstatusResultat.setHarFlyktningstatus(person.getFlyktning());
            hentPersonstatusResultat.setGjeldendeOppholdsstatus(gjeldendeOppholdStatusWsConverter.convert(person.getOppholdStatus()));

            hentPersonstatusResultat.setUavklartFlyktningstatus(person.getAvgjoerelseUavklart());
            hentPersonstatusResultat.setSoknadOmBeskyttelseUnderBehandling(
                    beskyttleseUnderBehandlingWsConverter.convert(person)
            );

            hentPersonstatusResultat.setUttrekkstidspunkt(xmlDateWsConverter.convert(LocalDate.now()));

            return hentPersonstatusResultat;
        }
        return null;
    }
}