package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer.Bruker;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.dto.IdentDTO;
import no.nav.pdl.forvalter.dto.Paginering;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.utils.RelasjonerAlderUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavPersonIdentifikatorDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.isNpidIdent;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private static final String INVALID_IDENT = "Ident må være på 11 tegn og numerisk";
    private static final String VIOLATION_ALIAS_EXISTS = "Utgått ident kan ikke endres. Benytt gjeldende ident %s for denne operasjonen";
    private static final String IDENT_ALREADY_EXISTS = "Ident %s eksisterer allerede i database";

    private static final String SORT_BY_FIELD = "sistOppdatert";

    private final PersonRepository personRepository;
    private final RelasjonRepository relasjonRepository;
    private final MergeService mergeService;
    private final PersonArtifactService personArtifactService;
    private final MapperFacade mapperFacade;
    private final IdentPoolConsumer identPoolConsumer;
    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final AliasRepository aliasRepository;
    private final ValidateArtifactsService validateArtifactsService;
    private final UnhookEksternePersonerService unhookEksternePersonerService;
    private final HendelseIdService hendelseIdService;

    @Transactional
    public Mono<String> updatePerson(String ident, PersonUpdateRequestDTO request, Boolean overwrite, Boolean relaxed) {

        return updatePersonInternal(ident, request, overwrite, relaxed);
    }

    private Mono<String> updatePersonInternal(String ident, PersonUpdateRequestDTO request, Boolean overwrite, Boolean relaxed) {

        if (!isNumeric(ident) || ident.length() != 11) {

            throw new InvalidRequestException(INVALID_IDENT);
        }

        return checkAlias(ident)
                .then(getDbPerson(ident, overwrite))
                .flatMap(dbPerson -> mergeService.merge(request.getPerson(), dbPerson.getPerson())
                        .flatMap(mergedPerson -> isNotTrue(relaxed) ?
                                validateArtifactsService.validate(mergedPerson)
                                        .then(Mono.just(mergedPerson)) : Mono.just(mergedPerson))
                        .flatMap(mergedPerson -> personArtifactService.buildPerson(mergedPerson, relaxed))
                        .map(extendedArtifacts -> {
                            dbPerson.setPerson(extendedArtifacts);
                            dbPerson.setFornavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getFornavn());
                            dbPerson.setMellomnavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getMellomnavn());
                            dbPerson.setEtternavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn());
                            dbPerson.setSistOppdatert(now());
                            return dbPerson;
                        }))
                .flatMap(personRepository::save)
                .map(DbPerson::getIdent);
    }

    @Transactional
    public Mono<Void> deletePerson(String ident) {

        var startTime = currentTimeMillis();

        return checkAlias(ident)
                .then(personRepository.findByIdent(ident))
                        .switchIfEmpty(Mono.error(new NotFoundException(format("Ident %s ble ikke funnet", ident))))
        .flatMap(dbPerson -> Mono.just(dbPerson)
                .doOnNext(this::deleteMasterPdlArtifacter)
                .doOnNext(this::deleteRelasjonerOgHendelser)
                .doOnNext(this::releaseIdents))

        // Identer som har blitt merget DNR/FNR <-> NPID kan ikke gjenbrukes da disse har blitt koblet permanent i PDL-aktoer
        var utgaatteIdenter = dbPerson.getAlias().stream()
                .sorted(Comparator.comparing(DbAlias::getSistOppdatert))
                .map(DbAlias::getTidligereIdent)
                .toList();

        var identerSomIkkeSkalSlettesFraIdentpool = new HashSet<String>();
        for (var i = 0; i < utgaatteIdenter.size(); i++) {

            if (isNpidIdent(utgaatteIdenter.get(i))) {
                identerSomIkkeSkalSlettesFraIdentpool.add(utgaatteIdenter.get(i));
                identerSomIkkeSkalSlettesFraIdentpool.add(i < utgaatteIdenter.size() - 1 ? utgaatteIdenter.get(i + 1) : dbPerson.getIdent());
            }
        }

        unhookEksternePersonerService.unhook(dbPerson);

        var identer = Stream.of(dbPerson.getRelasjoner().stream(),
                        dbPerson.getRelasjoner().stream()
                                .map(DbRelasjon::getRelatertPerson)
                                .map(DbPerson::getRelasjoner)
                                .flatMap(Collection::stream))
                .flatMap(Function.identity())
                .map(DbRelasjon::getRelatertPerson)
                .map(DbPerson::getIdent)
                .collect(Collectors.toSet());

        pdlTestdataConsumer.delete(identer).block();
        identPoolConsumer.releaseIdents(identer.stream()
                .filter(id -> !identerSomIkkeSkalSlettesFraIdentpool.contains(id))
                .collect(Collectors.toSet()), Bruker.PDLF).block();

        personRepository.deleteByIdentIn(identer);
        log.info("Sletting av ident {} tok {} ms", ident, currentTimeMillis() - startTime);
    }

    private Mono<List> getUtgaatteIdenter(DbPerson dbPerson) {

        return aliasRepository.findByTidligereIdentIn(dbPerson.getAlias().stream()
                        .map(DbAlias::getTidligereIdent)
                        .toList())
                .map(DbAlias::getTidligereIdent)
                .collectList();
    }


    @Transactional(readOnly = true)
    public Flux<FullPersonDTO> getPerson(List<String> identer, Paginering paginering) {

        if (nonNull(identer) && !identer.isEmpty()) {

            return Mono.zip(aliasRepository.findByTidligereIdentIn(identer)
                                    .map(DbAlias::getPerson)
                                    .map(DbPerson::getIdent)
                                    .collect(HashSet<String>::new, Set::add),
                            aliasRepository.findByTidligereIdentIn(identer)
                                    .map(DbAlias::getTidligereIdent)
                                    .collect(HashSet<String>::new, Set::add))
                    .map(tilleggOgFjerning -> {
                        val query = new HashSet<>(identer);
                        query.addAll(tilleggOgFjerning.getT1());
                        query.removeAll(tilleggOgFjerning.getT2());
                        return query;
                    })
                    .flatMapMany(query -> personRepository.findByIdentIn(query,
                            PageRequest.of(paginering.getSidenummer(),
                                    paginering.getSidestoerrelse(),
                                    Sort.by(SORT_BY_FIELD).descending())))
                    .map(person ->
                            mapperFacade.map(person, FullPersonDTO.class));

        } else

            return personRepository.findAll(
                            PageRequest.of(paginering.getSidenummer(),
                                    paginering.getSidestoerrelse(),
                                    Sort.by(SORT_BY_FIELD).descending()))
                    .map(person -> mapperFacade.map(person, FullPersonDTO.class));
    }

    @Transactional
    public Mono<String> createPerson(BestillingRequestDTO request) {

        if (isNull(request.getPerson())) {
            request.setPerson(new PersonDTO());
        }
        RelasjonerAlderUtility.fixRelasjonerAlder(request);

        return acquireIdentifier(request)
                .doOnNext(identifier -> request.getPerson().setIdent(identifier.getIdent()))
                .doOnNext(identifier -> {

                    if (request.getPerson().getKjoenn().isEmpty()) {
                        request.getPerson().getKjoenn().add(new KjoennDTO());
                    }
                    if (request.getPerson().getFoedselsdato().isEmpty()) {
                        request.getPerson().getFoedselsdato().add(FoedselsdatoDTO.builder()
                                .foedselsdato(nonNull(identifier) && nonNull(identifier.getFoedselsdato()) ?
                                        identifier.getFoedselsdato().atStartOfDay() : null)
                                .build());
                    }
                    if (request.getPerson().getFoedested().isEmpty()) {
                        request.getPerson().getFoedested().add(new FoedestedDTO());
                    }
                    if (request.getPerson().getNavn().isEmpty()) {
                        request.getPerson().getNavn().add(new NavnDTO());
                    }
                    if (request.getPerson().getBostedsadresse().isEmpty()) {
                        request.getPerson().getBostedsadresse().add(new BostedadresseDTO());
                    }
                    if (request.getPerson().getStatsborgerskap().isEmpty()) {
                        request.getPerson().getStatsborgerskap().add(new StatsborgerskapDTO());
                    }
                    if (request.getPerson().getSivilstand().stream().noneMatch(SivilstandDTO::isUgift)) {
                        request.getPerson().getSivilstand().addFirst(new SivilstandDTO());
                    }
                    if (Identtype.NPID == getIdenttype(request.getPerson().getIdent())) {
                        request.getPerson().getNavPersonIdentifikator().add(new NavPersonIdentifikatorDTO());
                    }
                })
                .flatMap(identifier -> updatePersonInternal(request.getPerson().getIdent(), PersonUpdateRequestDTO.builder()
                        .person(request.getPerson())
                        .build(), null, null));
    }

    private Mono<IdentDTO> acquireIdentifier(BestillingRequestDTO request) {

        if (isBlank(request.getOpprettFraIdent())) {
            return identPoolConsumer.acquireIdent(
                            mapperFacade.map(request, HentIdenterRequest.class))
                    .doOnNext(identifier ->
                            Objects.requireNonNull(identifier, "Personident fra identpool kan ikke være null"));

        } else {
            return personRepository.existsByIdent(request.getOpprettFraIdent())
                    .flatMap(exists -> isTrue(exists) ?
                            Mono.error(new InvalidRequestException(format(IDENT_ALREADY_EXISTS, request.getOpprettFraIdent()))) :
                            identPoolConsumer.allokerIdent(request.getOpprettFraIdent())
                                    .then(Mono.just(IdentDTO.builder()
                                            .ident(request.getOpprettFraIdent())
                                            .build())));
        }
    }

    private Mono<Void> checkAlias(String ident) {

        return aliasRepository.findByTidligereIdent(ident)
                .flatMap(eksisterendeIdent -> Mono.error(
                        new InvalidRequestException(format(VIOLATION_ALIAS_EXISTS, ident))))
                .then();
    }

    private Mono<DbPerson> getDbPerson(String ident, Boolean overwrite) {

        return Mono.just(overwrite)
                .flatMap(ow -> isTrue(ow) ?
                        personRepository.deleteByIdent(ident) : Mono.empty())
                .then(personRepository.findByIdent(ident)
                        .switchIfEmpty(personRepository.save(DbPerson.builder()
                                .ident(ident)
                                .person(PersonDTO.builder()
                                        .ident(ident)
                                        .build())
                                .sistOppdatert(now())
                                .build())));
    }

    @Transactional
    public void deleteMasterPdlArtifacter(String ident) {

        personRepository.findByIdent(ident)
                .ifPresentOrElse(person -> {
                            hendelseIdService.deletePdlHendelser(person);
                            unhookEksternePersonerService.unhook(person);

                            relasjonRepository.deleteByPersonIdentIn(
                                    person.getRelasjoner().stream()
                                            .map(DbRelasjon::getRelatertPerson)
                                            .filter(Objects::nonNull)
                                            .map(DbPerson::getIdent)
                                            .toList());

                            personRepository.deleteByIdent(ident);
                        },
                        () -> {
                            throw new NotFoundException(format("Ident %s ble ikke funnet", ident));
                        });
    }
}