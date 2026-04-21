package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnhookEksternePersonerService {

    private final PersonRepository personRepository;
    private final RelasjonRepository relasjonRepository;

    public Mono<Void> unhook(DbPerson hovedperson) {

        return relasjonRepository.findByPersonId(hovedperson.getId())
                .map(DbRelasjon::getRelatertPersonId)
                .distinct()
                .flatMap(personRepository::findById)
                .collectList()
                .map(relatertePersoner -> {
                    val allePersoner = new ArrayList<>(relatertePersoner);
                    allePersoner.addFirst(hovedperson);
                    return allePersoner;
                })
                .flatMap(this::unhookStandalone)
                .flatMap(this::deleteStandaloneRelasjoner);
    }

    private Mono<List<Long>> unhookStandalone(List<DbPerson> allePersoner) {

        return Flux.fromIterable(allePersoner)
                .filter(dbPerson -> isTrue(dbPerson.getPerson().getStandalone()))
                .flatMap(dbPerson -> Flux.fromArray(dbPerson.getPerson().getClass().getMethods())
                        .filter(method -> method.getName().startsWith("get"))
                        .filter(method -> method.getReturnType().equals(List.class))
                        .flatMap(metodeForOpplysninger -> {
                            val personOpplysninger = keepOpplysning(metodeForOpplysninger, dbPerson, allePersoner);
                            try {
                                dbPerson.getPerson().getClass().getMethod(metodeForOpplysninger.getName()
                                                .replace("get", "set"), List.class)
                                        .invoke(dbPerson.getPerson(), personOpplysninger);
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                log.error("Feilet å sette personopplysninger for {} ", metodeForOpplysninger);
                            }
                            return Mono.empty();
                        })
                        .then(Mono.just(dbPerson)))
                .flatMap(personRepository::save)
                .map(DbPerson::getId)
                .collectList();
    }

    private List<? extends DbVersjonDTO> keepOpplysning(Method metode, DbPerson standalonePerson, List<DbPerson> allePersoner) {

        val opplysninger = new ArrayList<DbVersjonDTO>();
        try {
            opplysninger.addAll((List<DbVersjonDTO>) metode.invoke(standalonePerson.getPerson()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Feilet å hente personopplysninger for {} ", standalonePerson.getPerson().getClass().getSimpleName() + "." + metode.getName());
        }
        val allePersonopplysninger = allePersoner.stream()
                .filter(dbPerson -> isNotTrue(dbPerson.getPerson().getStandalone()))
                .collect(Collectors.toMap(DbPerson::getIdent, dbPerson -> {
                    try {
                        return (List<DbVersjonDTO>) metode.invoke(dbPerson.getPerson());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("Feilet å hente personopplysninger for {} ", standalonePerson.getPerson().getClass().getSimpleName() + "." + metode.getName());
                        return emptyList();
                    }
                }));

        return opplysninger.stream()
                .filter(opplysning1 -> allePersonopplysninger.values().stream()
                        .noneMatch(personOpplysninger ->
                                personOpplysninger.stream().noneMatch(opplysning2 ->
                                        Objects.equals(opplysning1.getIdentForRelasjon(),
                                                opplysning2 instanceof DbVersjonDTO dbVersjonDTO ?
                                                        dbVersjonDTO.getIdentForRelasjon() : null))))
                .toList();
    }

    private Mono<Void> deleteStandaloneRelasjoner(List<Long> standalonePersonIder) {

        return Flux.fromIterable(standalonePersonIder)
                .flatMap(relasjonRepository::deleteByPersonIdOrRelatertPersonId)
                .then();
    }
}
