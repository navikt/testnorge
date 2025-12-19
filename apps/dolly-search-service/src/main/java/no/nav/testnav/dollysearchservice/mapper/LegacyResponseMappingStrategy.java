package no.nav.testnav.dollysearchservice.mapper;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.dollysearchservice.dto.Person;
import no.nav.testnav.libs.dto.dollysearchservice.v1.legacy.PersonDTO;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Component
@RequiredArgsConstructor
public class LegacyResponseMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(JsonNode.class, PersonDTO.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(JsonNode response, PersonDTO personDTO, MappingContext context) {

                        var person = objectMapper.convertValue(response, Person.class);
                        personDTO.setTags(person.getTags());
                        mapIdent(personDTO, person);
                        mapAktoerId(personDTO, person);
                        mapNavn(personDTO, person);
                        mapFoedselsdato(personDTO, person);
                        mapKjoenn(personDTO, person);
                        mapStatsborgerskap(personDTO, person);
                        mapSivilstand(personDTO, person);
                        mapPersonstatus(personDTO, person);
                        mapDoedsdato(personDTO, person);
                        mapInnflyttingTilNorge(personDTO, person);
                        mapUtflyttingFraNorge(personDTO, person);
                        mapForelderBarnRelasjoner(personDTO, person);
                    }
                })
                .register();
    }

    private void mapDoedsdato(PersonDTO personDTO, Person person) {

        person.getHentPerson().getDoedsfall().stream()
                .filter(doedsfall -> nonNull(doedsfall.getDoedsdato()))
                .findFirst()
                .ifPresent(doedsfall -> personDTO.setDoedsfall(PersonDTO.DoedsfallDTO.builder()
                        .doedsdato(doedsfall.getDoedsdato())
                        .build()));
    }

    private void mapInnflyttingTilNorge(PersonDTO personDTO, Person person) {

        person.getHentPerson().getInnflyttingTilNorge().stream()
                .findFirst()
                .ifPresent(innflyttingTilNorge -> personDTO.setInnfyttingTilNorge(PersonDTO.InnflyttingTilNorgeDTO.builder()
                        .fraflyttingsland(innflyttingTilNorge.getFraflyttingsland())
                        .fraflyttingsstedIUtlandet(innflyttingTilNorge.getFraflyttingsstedIUtlandet())
                        .build()));
    }

    private void mapUtflyttingFraNorge(PersonDTO personDTO, Person person) {

        person.getHentPerson().getUtflyttingFraNorge().stream()
                .findFirst()
                .ifPresent(utflyttingFraNorge -> personDTO.setUtfyttingFraNorge(PersonDTO.UtfyttingFraNorgeDTO.builder()
                        .tilflyttingsland(utflyttingFraNorge.getTilflyttingsland())
                        .tilflyttingsstedIUtlandet(utflyttingFraNorge.getTilflyttingsstedIUtlandet())
                        .utflyttingsdato(utflyttingFraNorge.getUtflyttingsdato())
                        .build()));
    }

    private void mapForelderBarnRelasjoner(PersonDTO personDTO, Person person) {

        if (!person.getHentPerson().getForelderBarnRelasjon().isEmpty()) {
            personDTO.setForelderBarnRelasjoner(PersonDTO.ForelderBarnRelasjonDTO.builder()
                            .barn(person.getHentPerson().getForelderBarnRelasjon().stream()
                                    .filter(Person.ForelderBarnRelasjon::isBarn)
                                    .map(Person.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                    .toList())
                            .foreldre(person.getHentPerson().getForelderBarnRelasjon().stream()
                                    .filter(Person.ForelderBarnRelasjon::isForelder)
                                    .map(forelderBarnRelasjon -> PersonDTO.ForelderDTO.builder()
                                            .ident(forelderBarnRelasjon.getRelatertPersonsIdent())
                                            .rolle(forelderBarnRelasjon.getRelatertPersonsRolle().name())
                                            .build())
                                    .toList())
                            .build());
        }
    }

    private static void mapIdent(PersonDTO personDTO, Person person) {

        personDTO.setIdent(person.getHentIdenter().getIdenter().stream()
                .filter(identer -> "FOLKEREGISTERIDENT".equals(identer.getGruppe()) &&
                        isFalse(identer.getHistorisk()))
                .map(Person.Identer::getIdent)
                .findFirst().orElse(null));
    }

    private static void mapAktoerId(PersonDTO personDTO, Person person) {

        person.getHentIdenter().getIdenter().stream()
                .filter(identer -> "AKTORID".equals(identer.getGruppe()) &&
                        isFalse(identer.getHistorisk()))
                .map(Person.Identer::getIdent)
                .findFirst()
                .ifPresent(personDTO::setAktorId);
    }

    private static void mapNavn(PersonDTO personDTO, Person person) {

        person.getHentPerson().getNavn().stream()
                .findFirst()
                .ifPresent(navn -> {
                    personDTO.setFornavn(navn.getFornavn());
                    personDTO.setMellomnavn(navn.getMellomnavn());
                    personDTO.setEtternavn(navn.getEtternavn());
                });
    }

    private static void mapFoedselsdato(PersonDTO personDTO, Person person) {

        person.getHentPerson().getFoedselsdato().stream()
                .findFirst()
                .ifPresent(foedselsdato ->
                        personDTO.setFoedselsdato(PersonDTO.FoedselsdatoDTO.builder()
                                .foedselsdato(foedselsdato.getFoedselsdato())
                                .build()));
    }

    private static void mapKjoenn(PersonDTO personDTO, Person person) {

        person.getHentPerson().getKjoenn().stream()
                .findFirst()
                .ifPresent(kjoenn ->
                        personDTO.setKjoenn(kjoenn.getKjoenn().name()));
    }

    private static void mapStatsborgerskap(PersonDTO personDTO, Person person) {

        if (!person.getHentPerson().getStatsborgerskap().isEmpty()) {
            personDTO.setStatsborgerskap(PersonDTO.StatsborgerskapDTO.builder()
                    .land(person.getHentPerson().getStatsborgerskap().stream()
                            .map(Person.Statsborgerskap::getLand)
                            .distinct()
                            .toList())
                    .build());
        }
    }

    private static void mapSivilstand(PersonDTO personDTO, Person person) {

        person.getHentPerson().getSivilstand().stream()
                .findFirst()
                .ifPresent(sivilstand ->
                        personDTO.setSivilstand(PersonDTO.SivilstandDTO.builder()
                                .type(sivilstand.getType().name())
                                .relatertVedSivilstand(sivilstand.getRelatertVedSivilstand())
                                .build()));
    }

    private static void mapPersonstatus(PersonDTO personDTO, Person person) {

        if (!person.getHentPerson().getFolkeregisterpersonstatus().isEmpty()) {
            personDTO.setFolkeregisterpersonstatus(person.getHentPerson().getFolkeregisterpersonstatus().stream()
                    .map(personstatus -> PersonDTO.FolkeregisterpersonstatusDTO.builder()
                            .status(personstatus.getStatus().name())
                            .gyldighetstidspunkt(personstatus.getFolkeregistermetadata().getGyldighetstidspunkt())
                            .build())
                    .toList());
        }
    }
}
