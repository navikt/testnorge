package no.nav.udistub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.udistub.database.model.Alias;
import no.nav.udistub.database.model.Arbeidsadgang;
import no.nav.udistub.database.model.Avgjorelse;
import no.nav.udistub.database.model.Periode;
import no.nav.udistub.database.model.Person;
import no.nav.udistub.database.model.PersonNavn;
import no.nav.udistub.database.model.opphold.IkkeOppholdstilatelseIkkeVilkaarIkkeVisum;
import no.nav.udistub.database.model.opphold.OppholdSammeVilkaar;
import no.nav.udistub.database.model.opphold.OppholdStatus;
import no.nav.udistub.database.repository.AliasRepository;
import no.nav.udistub.database.repository.ArbeidsAdgangRepository;
import no.nav.udistub.database.repository.AvgjorelseRepository;
import no.nav.udistub.database.repository.KodeverkRepository;
import no.nav.udistub.database.repository.OppholdStatusRepository;
import no.nav.udistub.database.repository.PersonRepository;
import no.nav.udistub.exception.NotFoundException;
import no.nav.udistub.service.dto.UdiAlias;
import no.nav.udistub.service.dto.UdiAvgjorelse;
import no.nav.udistub.service.dto.UdiPerson;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

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

        saveAliases(udiPerson.getAliaser(), person);

        saveAvgjoerelser(udiPerson.getAvgjoerelser(), person);

        saveOppholdsstatus(udiPerson, person);

        saveArbeidsadgang(udiPerson, person);

        return personRepository.findById(person.getId()).map(found -> mapperFacade.map(found, UdiPerson.class));
    }

    public Optional<UdiPerson> oppdaterPerson(UdiPerson udiPerson) {
        var person = personRepository.findByIdent(udiPerson.getIdent()).orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne person med fnr:%s", udiPerson.getIdent())));

        saveAvgjoerelser(udiPerson.getAvgjoerelser(), person);
        saveAliases(udiPerson.getAliaser(), person);

        var arbeidsadgang = person.getArbeidsadgang();
        var udiPersonArbeidsadgang = udiPerson.getArbeidsadgang();
        if (arbeidsadgang != null && udiPersonArbeidsadgang != null) {
            arbeidsadgang.setHarArbeidsAdgang(udiPersonArbeidsadgang.getHarArbeidsAdgang());
            arbeidsadgang.setTypeArbeidsadgang(udiPersonArbeidsadgang.getTypeArbeidsadgang());
            arbeidsadgang.setArbeidsOmfang(udiPersonArbeidsadgang.getArbeidsOmfang());
            arbeidsadgang.setPeriode(mapperFacade.map(udiPersonArbeidsadgang.getPeriode(), Periode.class));
            arbeidsAdgangRepository.save(arbeidsadgang);
        } else {
            saveArbeidsadgang(udiPerson, person);
        }

        var oppholdStatus = person.getOppholdStatus();
        var udiPersonOppholdStatus = udiPerson.getOppholdStatus();
        if (oppholdStatus != null && udiPersonOppholdStatus != null) {
            oppholdStatus.setUavklart(udiPersonOppholdStatus.getUavklart());
            oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrettPeriode(mapperFacade.map(udiPersonOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettPeriode(), Periode.class));
            oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrettEffektuering(udiPersonOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrettEffektuering());
            oppholdStatus.setEosEllerEFTABeslutningOmOppholdsrett(udiPersonOppholdStatus.getEosEllerEFTABeslutningOmOppholdsrett());
            oppholdStatus.setEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(mapperFacade.map(udiPersonOppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettPeriode(), Periode.class));
            oppholdStatus.setEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering(udiPersonOppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrettEffektuering());
            oppholdStatus.setEosEllerEFTAVedtakOmVarigOppholdsrett(udiPersonOppholdStatus.getEosEllerEFTAVedtakOmVarigOppholdsrett());
            oppholdStatus.setEosEllerEFTAOppholdstillatelsePeriode(mapperFacade.map(udiPersonOppholdStatus.getEosEllerEFTAOppholdstillatelsePeriode(), Periode.class));
            oppholdStatus.setEosEllerEFTAOppholdstillatelseEffektuering(udiPersonOppholdStatus.getEosEllerEFTAOppholdstillatelseEffektuering());
            oppholdStatus.setEosEllerEFTAOppholdstillatelse(udiPersonOppholdStatus.getEosEllerEFTAOppholdstillatelse());
            oppholdStatus.setOppholdSammeVilkaar(mapperFacade.map(udiPersonOppholdStatus.getOppholdSammeVilkaar(), OppholdSammeVilkaar.class));
            oppholdStatus.setIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(mapperFacade.map(udiPersonOppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum(), IkkeOppholdstilatelseIkkeVilkaarIkkeVisum.class));
            oppholdStatusRepository.save(oppholdStatus);
        } else {
            saveOppholdsstatus(udiPerson, person);
        }

        person.setAvgjoerelseUavklart(udiPerson.getAvgjoerelseUavklart());
        person.setHarOppholdsTillatelse(udiPerson.getHarOppholdsTillatelse());
        person.setFlyktning(udiPerson.getFlyktning());
        person.setSoeknadOmBeskyttelseUnderBehandling(udiPerson.getSoeknadOmBeskyttelseUnderBehandling());
        person.setSoknadDato(udiPerson.getSoknadDato());

        personRepository.save(person);

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

    private void saveAliases(
            List<UdiAlias> aliaser,
            Person person
    ) {
        if (aliaser != null) {
            aliasRepository.saveAll(aliaser.stream()
                    .map(alias -> mapperFacade.map(alias, Alias.class))
                    .peek(alias -> alias.setPerson(person))
                    .collect(Collectors.toList())
            );
        }
    }

    private void saveAvgjoerelser(
            List<UdiAvgjorelse> avgjoerelser,
            Person person
    ) {
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
    }

    private void saveOppholdsstatus(
            UdiPerson udiPerson,
            Person person
    ) {
        if (udiPerson.getOppholdStatus() != null) {
            OppholdStatus oppholdStatus = mapperFacade.map(udiPerson.getOppholdStatus(), OppholdStatus.class);
            oppholdStatus.setPerson(person);
            oppholdStatusRepository.save(oppholdStatus);
        }
    }

    private void saveArbeidsadgang(
            UdiPerson udiPerson,
            Person person
    ) {
        if (udiPerson.getArbeidsadgang() != null) {
            Arbeidsadgang arbeidsadgang = mapperFacade.map(udiPerson.getArbeidsadgang(), Arbeidsadgang.class);
            arbeidsadgang.setPerson(person);
            arbeidsAdgangRepository.save(arbeidsadgang);
        }
    }
}
