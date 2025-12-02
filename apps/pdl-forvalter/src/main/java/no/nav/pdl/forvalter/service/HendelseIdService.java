package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HendelseIdRequest;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class HendelseIdService {

    private final PersonRepository personRepository;
    private final PdlTestdataConsumer pdlTestdataConsumer;

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

    public void deletePdlHendelser(DbPerson person) {

        if (isTestnorgeIdent(person.getIdent())) {
            var relevanteHendelser = Stream.of(
                            getPdlHendelser(person.getIdent()).stream()
                                    .map(hendelse -> HendelseIdRequest.builder()
                                            .ident(person.getIdent())
                                            .hendelseId(hendelse.getHendelseId())
                                            .build()),
                            person.getRelasjoner().stream()
                                    .map(DbRelasjon::getRelatertPerson)
                                    .map(DbPerson::getPerson)
                                    .map(relatert -> getHendelser(relatert.getIdent(), relatert.getSivilstand()))
                                    .flatMap(Collection::stream),
                            person.getRelasjoner().stream()
                                    .map(DbRelasjon::getRelatertPerson)
                                    .map(DbPerson::getPerson)
                                    .map(relatert -> getHendelser(relatert.getIdent(), relatert.getForelderBarnRelasjon()))
                                    .flatMap(Collection::stream),
                            person.getRelasjoner().stream()
                                    .map(DbRelasjon::getRelatertPerson)
                                    .map(DbPerson::getPerson)
                                    .map(relatert -> getHendelser(relatert.getIdent(), relatert.getFullmakt()))
                                    .flatMap(Collection::stream)
                    )
                    .flatMap(Function.identity())
                    .toList();

            Flux.fromIterable(relevanteHendelser)
                    .flatMap(pdlTestdataConsumer::deleteHendelse)
                    .collectList()
                    .block();
        }
    }

    private List<HendelseIdRequest> getHendelser(String ident, List<? extends DbVersjonDTO> opplysninger) {

        return opplysninger.stream()
                .filter(DbVersjonDTO::isPdlMaster)
                .map(opplysning -> HendelseIdRequest.builder()
                        .ident(ident)
                        .hendelseId(opplysning.getHendelseId())
                        .build())
                .toList();
    }

    public void deletePdlHendelse(String ident, String artifact, Integer id) {

        if (isTestnorgeIdent(ident)) {

            var hendelse = personRepository.findByIdent(ident)
                    .map(DbPerson::getPerson)
                    .map(person -> {
                        try {
                            var method = person.getClass().getMethod("get" + artifact);
                            return (List<DbVersjonDTO>) method.invoke(person);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            log.error("Feilet å hente get{} fra person med ident {}", artifact, ident);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .flatMap(opplysninger -> opplysninger.stream()
                            .filter(opplysning -> id.equals(opplysning.getId()))
                            .filter(DbVersjonDTO::isPdlMaster)
                            .map(DbVersjonDTO::getHendelseId)
                            .filter(StringUtils::isNotBlank)
                            .findFirst())
                    .orElse(null);

            if (isNotBlank(hendelse)) {

                pdlTestdataConsumer.deleteHendelse(ident, hendelse)
                        .block();
            }
        }
    }
}
