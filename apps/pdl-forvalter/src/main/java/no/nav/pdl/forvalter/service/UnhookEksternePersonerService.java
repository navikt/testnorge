package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.AVDOEDD_FOR_KONTAKT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FAMILIERELASJON_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FULLMAKTSGIVER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FULLMEKTIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.KONTAKT_FOR_DOEDSBO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE_MOTTAKER;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnhookEksternePersonerService {

    private final PersonRepository personRepository;
    private final RelasjonRepository relasjonRepository;

    public Mono<Void> unhook(DbPerson hovedperson) {

        val alleRelasjoner = new AtomicReference<>(new HashSet<DbRelasjon>());
        return relasjonRepository.findByPersonId(hovedperson.getId())
                .doOnNext(relasjon -> alleRelasjoner.get().add(relasjon))
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
                .flatMap(standalonePartnereIds ->
                        deleteStandaloneRelasjoner(alleRelasjoner.get(), standalonePartnereIds));
    }

    private Mono<List<Long>> unhookStandalone(List<DbPerson> allePersoner) {

        return Flux.fromIterable(allePersoner)
                .filter(dbPerson -> isTrue(dbPerson.getPerson().getStandalone()))
                .flatMap(dbPerson -> Flux.fromArray(dbPerson.getPerson().getClass().getMethods())
                        .filter(method -> method.getName().startsWith("get"))
                        .filter(method -> method.getReturnType().equals(List.class))
                        .doOnNext(metodeForOpplysninger -> {
                            val personOpplysninger = keepOpplysning(metodeForOpplysninger, dbPerson, allePersoner);
                            try {
                                dbPerson.getPerson().getClass().getMethod(metodeForOpplysninger.getName().replace("get", "set"), List.class)
                                        .invoke(dbPerson.getPerson(), personOpplysninger);
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collectList()
                        .thenReturn(dbPerson))
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
                        throw new RuntimeException(e);
                    }
                }));

        return opplysninger.stream()
                .filter(opplysning1 -> allePersonopplysninger.values().stream()
                        .noneMatch(personOpplysninger ->
                                personOpplysninger.stream().anyMatch(opplysning2 ->
                                        !Objects.equals(opplysning1.getIdentForRelasjon(), opplysning2.getIdentForRelasjon()))))
                .toList();
    }

    private Mono<Void> deleteStandaloneRelasjoner(Set<DbRelasjon> alleRelasjoner, List<Long> standalonePersonIder) {

        return Flux.fromIterable(alleRelasjoner)
                .filter(relasjon -> standalonePersonIder.stream()
                        .anyMatch(id -> id.equals(relasjon.getRelatertPersonId()) ||
                                id.equals(relasjon.getPersonId())))
                .flatMap(relasjonRepository::delete)
                .then();
    }
}
