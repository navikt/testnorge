package no.nav.pdl.forvalter.service;

import io.r2dbc.spi.R2dbcException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonIDDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentitetService {

    private static final String FRAGMENT_INVALID = "Søkefragment kan ikke være tomt";

    private static final String SORT_BY_FIELD = "sistOppdatert";

    private final PersonRepository personRepository;
    private final RelasjonRepository relasjonRepository;
    private final ReactiveTransactionManager transactionManager;

    @Transactional(readOnly = true)
    public Flux<PersonIDDTO> getfragment(String fragment, Paginering paginering) {

        if (isBlank(fragment)) {
            return Flux.error(new InvalidRequestException(FRAGMENT_INVALID));
        }

        return searchPerson(fragment, paginering)
                .map(person -> PersonIDDTO.builder()
                        .ident(person.getIdent())
                        .fornavn(person.getFornavn())
                        .mellomnavn(person.getMellomnavn())
                        .etternavn(person.getEtternavn())
                        .build());
    }

    private Flux<DbPerson> searchPerson(String query, Paginering paginering) {

        Optional<String> ident = Stream.of(query.split(" "))
                .filter(StringUtils::isNumeric)
                .findFirst();

        List<String> navn = Stream.of(query.split(" "))
                .filter(fragment -> isNotBlank(fragment) && !StringUtils.isNumeric(fragment))
                .toList();

        return personRepository.findByWildcardIdent(ident.orElse(null),
                !navn.isEmpty() ? navn.getFirst().toUpperCase() : null,
                navn.size() > 1 ? navn.get(1).toUpperCase() : null,
                PageRequest.of(paginering.getSidenummer(),
                        paginering.getSidestoerrelse(),
                        Sort.by(SORT_BY_FIELD).descending()));
    }

    public Mono<Void> updateStandalone(String ident, Boolean standalone) {

        return Mono.defer(() -> personRepository.findByIdent(ident)
                        .switchIfEmpty(Mono.error(new NotFoundException("Ident " + ident + " ikke funnet")))
                        .doOnNext(dbPerson -> dbPerson.getPerson().setStandalone(standalone))
                        .flatMap(personRepository::save)
                        .flatMapMany(this::setStandaloneRelasjoner)
                        .then())
                .as(TransactionalOperator.create(transactionManager)::transactional)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .filter(this::isDeadlock)
                        .onRetryExhaustedThrow((_, signal) -> signal.failure()));
    }

    private boolean isDeadlock(Throwable throwable) {

        for (Throwable cause = throwable; nonNull(cause); cause = cause.getCause()) {
            if (cause instanceof R2dbcException r2dbcException &&
                "40P01".equals(r2dbcException.getSqlState())) {
                return true;
            }
        }
        return false;
    }

    private Flux<DbPerson> setStandaloneRelasjoner(DbPerson person) {

        val relatertId = new AtomicReference<>(new HashSet<Long>());
        return relasjonRepository.findByPersonId(person.getId())
                .filter(relasjon -> !relatertId.get().contains(relasjon.getRelatertPersonId()))
                .doOnNext(relasjon -> relatertId.get().add(relasjon.getRelatertPersonId()))
                .concatMap(relasjon ->
                        setStandalonePerson(relasjon.getRelatertPersonId(), person.getIdent(), person.getPerson().isStandalone()));
    }

    private Mono<DbPerson> setStandalonePerson(Long id, String motpartsIdent, Boolean standalone) {

        return personRepository.findById(id)
                        .flatMap(dbPerson -> updateStandaloneRelasjon(dbPerson, motpartsIdent, standalone))
                        .flatMap(personRepository::save);
    }

    private Mono<DbPerson> updateStandaloneRelasjon(DbPerson dbPerson, String motpartsIdent, Boolean standalone) {

        return Flux.fromArray(PersonDTO.class.getMethods())
                .filter(method -> method.getName().contains("get"))
                .filter(method -> method.getReturnType().equals(List.class))
                .flatMap(method -> {
                    try {
                        val opplysninger = (List<DbVersjonDTO>) method.invoke(dbPerson.getPerson());
                        return Flux.fromIterable(opplysninger);
                    } catch (IllegalAccessException | InvocationTargetException _) {
                        log.error("Feilet å utføre metodekall for {} ", method);
                        return Mono.empty();
                    }
                })
                .doOnNext(opplysning -> {
                    if (motpartsIdent.equals(opplysning.getIdentForRelasjon())) {
                        try {
                            var method = opplysning.getClass().getMethod("setEksisterendePerson", Boolean.class);
                            method.invoke(opplysning, isTrue(standalone));

                        } catch (NoSuchMethodException | InvocationTargetException |
                                 IllegalAccessException _) {
                            log.error("Method setEksisterendePerson not found in {}", opplysning);
                        }
                    }
                })
                .collectList()
                .thenReturn(dbPerson);
    }
}