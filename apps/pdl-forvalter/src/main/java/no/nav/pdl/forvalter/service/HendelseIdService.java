package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HendelseIdDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
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

    public List<HendelseIdDTO> getAllePdlHendelser(String ident) {

        var hendelser = new AtomicReference<List<HendelseIdDTO>>();
        personRepository.findByIdent(ident)
                .ifPresentOrElse(hovedperson -> hendelser.set(Stream.of(
                                        Stream.of(hovedperson)
                                                .map(DbPerson::getPerson)
                                                .flatMap(person -> Arrays.stream(person.getClass().getMethods())
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
                                                        .map(artifact -> HendelseIdDTO.builder()
                                                                .ident(ident)
                                                                .hendelseId(artifact.getHendelseId())
                                                                .master(artifact.getMaster())
                                                                .build())
                                                ),

                                        // Handling of related persons
                                        hovedperson.getRelasjoner().stream()
                                                .map(DbRelasjon::getRelatertPerson)
                                                .map(DbPerson::getPerson)
                                                .flatMap(person -> Stream.of(person.getFullmakt().stream()
                                                                        .filter(fullmakt -> ident.equals(fullmakt.getIdentForRelasjon()))
                                                                        .map(fullmakt -> HendelseIdDTO.builder()
                                                                                .ident(person.getIdent())
                                                                                .hendelseId(fullmakt.getHendelseId())
                                                                                .master(fullmakt.getMaster())
                                                                                .build())
                                                                        .toList(),
                                                                person.getSivilstand().stream()
                                                                        .filter(sivilstand -> ident.equals(sivilstand.getIdentForRelasjon()))
                                                                        .map(sivilstand -> HendelseIdDTO.builder()
                                                                                .ident(person.getIdent())
                                                                                .hendelseId(sivilstand.getHendelseId())
                                                                                .master(sivilstand.getMaster())
                                                                                .build())
                                                                        .toList(),
                                                                person.getForelderBarnRelasjon().stream()
                                                                        .filter(relasjon -> ident.equals(relasjon.getIdentForRelasjon()))
                                                                        .map(relasjon -> HendelseIdDTO.builder()
                                                                                .ident(person.getIdent())
                                                                                .hendelseId(relasjon.getHendelseId())
                                                                                .master(relasjon.getMaster())
                                                                                .build())
                                                                        .toList())
                                                        .flatMap(Collection::stream))
                                )
                                .flatMap(Function.identity())
                                .filter(HendelseIdDTO::isPdlMaster)
                                .filter(dbVersjonDTO -> isNotBlank(dbVersjonDTO.getHendelseId()))
                                .toList()),

                        () -> hendelser.set(Collections.emptyList()));

        return hendelser.get();
    }


    public void deletePdlHendelser(String ident) {

        if (isTestnorgeIdent(ident)) {

            Flux.fromIterable(getAllePdlHendelser(ident))
                    .flatMap(hendelse -> pdlTestdataConsumer.deleteHendelse(hendelse.getIdent(), hendelse.getHendelseId()))
                    .collectList()
                    .block();
        }
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
