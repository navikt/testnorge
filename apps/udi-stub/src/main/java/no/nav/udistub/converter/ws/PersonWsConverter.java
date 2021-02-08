package no.nav.udistub.converter.ws;

import no.nav.udistub.service.dto.UdiAvgjorelse;
import no.nav.udistub.service.dto.UdiPerson;
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PersonWsConverter implements Converter<UdiPerson, HentPersonstatusResultat> {
    @Override
    public HentPersonstatusResultat convert(UdiPerson person) {
        if (person != null) {
            XmlDateWsConverter xmlDateWsConverter = new XmlDateWsConverter();
            GjeldendePersonWsConverter gjeldendePersonWsConverter = new GjeldendePersonWsConverter();
            ArbeidsadgangWsConverter arbeidsadgangWsConverter = new ArbeidsadgangWsConverter();
            AvgjorelsehistorikkWsConverter avgjorelsehistorikkWsConverter = new AvgjorelsehistorikkWsConverter();
            HentPersonstatusResultat hentPersonstatusResultat = new HentPersonstatusResultat();
            BeskyttleseUnderBehandlingWsConverter beskyttleseUnderBehandlingWsConverter = new BeskyttleseUnderBehandlingWsConverter();
            GjeldendeOppholdStatusWsConverter gjeldendeOppholdStatusWsConverter = new GjeldendeOppholdStatusWsConverter();

            hentPersonstatusResultat.setGjeldendePerson(gjeldendePersonWsConverter.convert(person));
            hentPersonstatusResultat.setArbeidsadgang(arbeidsadgangWsConverter.convert(person.getArbeidsadgang()));
            hentPersonstatusResultat.setAvgjorelsehistorikk(avgjorelsehistorikkWsConverter.convert(person));
            hentPersonstatusResultat.setForesporselsfodselsnummer(person.getIdent());
            hentPersonstatusResultat.setHarFlyktningstatus(person.getFlyktning());
            hentPersonstatusResultat.setGjeldendeOppholdsstatus(gjeldendeOppholdStatusWsConverter.convert(person.getOppholdStatus()));
            hentPersonstatusResultat.setHistorikkHarFlyktningstatus(person.getAvgjoerelser()
                    .stream()
                    .anyMatch(UdiAvgjorelse::getHarFlyktningstatus));

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