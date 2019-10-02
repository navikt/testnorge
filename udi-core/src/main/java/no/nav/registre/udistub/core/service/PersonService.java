package no.nav.registre.udistub.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import no.nav.registre.udistub.core.database.model.Alias;
import no.nav.registre.udistub.core.database.model.Arbeidsadgang;
import no.nav.registre.udistub.core.database.model.Avgjorelse;
import no.nav.registre.udistub.core.database.model.Person;
import no.nav.registre.udistub.core.database.model.PersonNavn;
import no.nav.registre.udistub.core.database.model.opphold.OppholdStatus;
import no.nav.registre.udistub.core.database.repository.AliasRepository;
import no.nav.registre.udistub.core.database.repository.ArbeidsAdgangRepository;
import no.nav.registre.udistub.core.database.repository.AvgjorelseRepository;
import no.nav.registre.udistub.core.database.repository.KodeverkRepository;
import no.nav.registre.udistub.core.database.repository.OppholdStatusRepository;
import no.nav.registre.udistub.core.database.repository.PersonRepository;
import no.nav.registre.udistub.core.exception.NotFoundException;
import no.nav.registre.udistub.core.service.to.UdiAlias;
import no.nav.registre.udistub.core.service.to.UdiAvgjorelse;
import no.nav.registre.udistub.core.service.to.UdiPerson;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final MapperFacade mapperFacade;

    private final PersonRepository personRepository;
    private final AliasRepository aliasRepository;
    private final AvgjorelseRepository avgjorelseRepository;
    private final OppholdStatusRepository oppholdStatusRepository;
    private final ArbeidsAdgangRepository arbeidsAdgangRepository;
    private final KodeverkRepository kodeverkRepository;

    public Optional<UdiPerson> opprettPerson(UdiPerson udiPerson) {

        Person nyPerson = Person.builder()
                .flyktning(udiPerson.getFlyktning())
                .avgjoerelseUavklart(udiPerson.getAvgjoerelseUavklart())
                .harOppholdsTillatelse(udiPerson.getHarOppholdsTillatelse())
                .ident(udiPerson.getIdent())
                .foedselsDato(udiPerson.getFoedselsDato())
                .navn(mapperFacade.map(udiPerson.getNavn(), PersonNavn.class))
                .soeknadOmBeskyttelseUnderBehandling(udiPerson.getSoeknadOmBeskyttelseUnderBehandling() != null ?
                        udiPerson.getSoeknadOmBeskyttelseUnderBehandling() : JaNeiUavklart.UAVKLART)
                .soknadDato(udiPerson.getSoknadDato() != null ? udiPerson.getSoknadDato() : LocalDate.now())
                .build();

        Person person = personRepository.save(nyPerson);
        List<UdiAlias> aliaser = udiPerson.getAliaser();
        if (aliaser != null) {
            aliasRepository.saveAll(aliaser.stream()
                    .map(alias -> mapperFacade.map(alias, Alias.class))
                    .peek(alias -> alias.setPerson(person))
                    .collect(Collectors.toList())
            );
        }
        List<UdiAvgjorelse> avgjoerelser = udiPerson.getAvgjoerelser();
        if (avgjoerelser != null) {
            avgjorelseRepository.saveAll(avgjoerelser.stream()
                    .map(avgjoerelse -> mapperFacade.map(avgjoerelse, Avgjorelse.class))
                    .peek(avgjoerelse -> {
                        avgjoerelse.setPerson(person);
                        if (avgjoerelse.getTillatelseKode() != null) {
                            avgjoerelse.setTillatelseKode(kodeverkRepository.findByKode(avgjoerelse.getTillatelseKode().getKode()).orElseThrow(
                                    () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ugyldig kode i tillatelse")
                            ));
                        }
                        if (avgjoerelse.getGrunntypeKode() != null) {
                            avgjoerelse.setGrunntypeKode(kodeverkRepository.findByKode(avgjoerelse.getGrunntypeKode().getKode()).orElseThrow(
                                    () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ugyldig kode i grunntype")
                            ));
                        }
                        if (avgjoerelse.getUtfallstypeKode() != null) {
                            avgjoerelse.setUtfallstypeKode(kodeverkRepository.findByKode(avgjoerelse.getUtfallstypeKode().getKode()).orElseThrow(
                                    () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ugyldig kode i utfallstype")
                            ));
                        }
                        if (avgjoerelse.getUtfallVarighetKode() != null) {
                            avgjoerelse.setUtfallVarighetKode(kodeverkRepository.findByKode(avgjoerelse.getUtfallVarighetKode().getKode()).orElseThrow(
                                    () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ugyldig kode i utfall varighet")
                            ));
                        }
                        if (avgjoerelse.getTillatelseVarighetKode() != null) {
                            avgjoerelse.setUtfallVarighetKode(kodeverkRepository.findByKode(avgjoerelse.getTillatelseVarighetKode().getKode()).orElseThrow(
                                    () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Ugyldig kode i tillatelse varighet")
                            ));
                        }
                    })
                    .collect(Collectors.toList())
            );
        }
        if (udiPerson.getOppholdStatus() != null) {
            OppholdStatus oppholdStatus = mapperFacade.map(udiPerson.getOppholdStatus(), OppholdStatus.class);
            oppholdStatus.setPerson(person);
            oppholdStatusRepository.save(oppholdStatus);
        }

        if (udiPerson.getArbeidsadgang() != null) {
            Arbeidsadgang arbeidsadgang = mapperFacade.map(udiPerson.getArbeidsadgang(), Arbeidsadgang.class);
            arbeidsadgang.setPerson(person);
            arbeidsAdgangRepository.save(arbeidsadgang);
        }

        return personRepository.findById(person.getId()).map(found -> mapperFacade.map(found, UdiPerson.class));
    }

    public Optional<UdiPerson> finnPerson(String ident) {
        return personRepository.findByIdent(ident).map(person -> mapperFacade.map(person, UdiPerson.class));
    }

    public void deletePerson(String ident) {
        Optional<Person> optionalPerson = personRepository.findByIdent(ident);
        if (optionalPerson.isPresent()) {
            personRepository.deleteById(optionalPerson.get().getId());
        } else {
            throw new NotFoundException(format("Kunne ikke slette person med ident:%s, da personen ikke ble funnet", ident));
        }
    }
}
