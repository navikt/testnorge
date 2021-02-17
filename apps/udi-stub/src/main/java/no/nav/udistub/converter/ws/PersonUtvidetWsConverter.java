package no.nav.udistub.converter.ws;

import lombok.RequiredArgsConstructor;
import no.nav.udistub.service.dto.UdiPerson;
import no.udi.mt_1067_nav_data.v1.HentUtvidetPersonstatusResultat;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class PersonUtvidetWsConverter implements Converter<UdiPerson, HentUtvidetPersonstatusResultat> {

    private final GjeldendeOppholdStatusWsConverter gjeldendeOppholdStatusWsConverter;
    private final XmlDateWsConverter xmlDateWsConverter;
    private final GjeldendePersonWsConverter gjeldendePersonWsConverter;
    private final ArbeidsadgangUtvidetWsConverter arbeidsadgangUtvidetWsConverter;
    private final BeskyttleseUnderBehandlingWsConverter beskyttleseUnderBehandlingWsConverter;

    @Override
    public HentUtvidetPersonstatusResultat convert(UdiPerson person) {

        if (nonNull(person)) {

            var hentPersonstatusResultat = new HentUtvidetPersonstatusResultat();

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