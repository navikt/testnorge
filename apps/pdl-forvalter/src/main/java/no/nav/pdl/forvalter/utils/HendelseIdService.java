package no.nav.pdl.forvalter.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class HendelseIdService {

    private final PersonRepository personRepository;

    public void oppdaterPerson(OrdreResponseDTO response) {

        Stream.of(List.of(response.getHovedperson()),
                        response.getRelasjoner())
                .flatMap(Collection::stream)
                .forEach(person -> personRepository.findByIdent(person.getIdent())
                        .ifPresent(dbPerson -> person.getOrdrer().stream()
                                .filter(OrdreResponseDTO.PdlStatusDTO::isDataElement)
                                .forEach(ordre -> {
                                    try {
                                        var getter = PersonDTO.class.getMethod("get" + ordre.getInfoElement().getDescription());
                                        var artifact = (List<DbVersjonDTO>) getter.invoke(dbPerson.getPerson());
                                        ordre.getHendelser()
                                                .forEach(hendelse -> artifact.stream()
                                                        .filter(fact -> hendelse.getId().equals(fact.getId()))
                                                        .findFirst()
                                                        .ifPresent(fact ->
                                                                fact.setHendelseId(hendelse.getHendelseId())));

                                    } catch (NoSuchMethodException |
                                             InvocationTargetException |
                                             IllegalAccessException e) {
                                        log.error("Feilet å lagre hendelseId, {}", e.getMessage());
                                    }
                                })));
    }

    public List<DbVersjonDTO> getPdlHendelser(String ident) {

        return personRepository.findByIdent(ident)
                .map(DbPerson::getPerson)
                .map(person -> Arrays.stream(person.getClass().getMethods())
                        .filter(method -> method.getName().contains("get"))
                        .map(method -> {
                            try {
                                return method.invoke(person);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                log.error("Feilet å hente hendelseId, {}", e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .filter(List.class::isInstance)
                        .map(value -> (List<DbVersjonDTO>) value)
                        .flatMap(Collection::stream)
                        .filter(DbVersjonDTO::isPdlMaster)
                        .filter(dbVersjonDTO -> isNotBlank(dbVersjonDTO.getHendelseId()))
                        .toList())
                .orElse(Collections.emptyList());
    }
}
