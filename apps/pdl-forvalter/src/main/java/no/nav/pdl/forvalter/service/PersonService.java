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
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.mapper.MappingContextUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.getIdenttype;
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

    private final AliasRepository aliasRepository;
    private final HendelseIdService hendelseIdService;
    private final IdentPoolConsumer identPoolConsumer;
    private final MapperFacade mapperFacade;
    private final MergeService mergeService;
    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final PersonArtifactService personArtifactService;
    private final PersonRepository personRepository;
    private final RelasjonRepository relasjonRepository;
    private final RelasjonerAlderService relasjonerAlderService;
    private final UnhookEksternePersonerService unhookEksternePersonerService;
    private final ValidateArtifactsService validateArtifactsService;

    @Transactional
    public Mono<String> updatePerson(String ident, PersonUpdateRequestDTO request, Boolean relaxed) {

        val now = System.currentTimeMillis();
        if (isNull(request.getPerson())) {
            request.setPerson(new PersonDTO());
        }

        return updatePersonInternal(ident, request, relaxed)
                .doOnNext(updatedIdent -> log.info("Oppdatering av person {} tok {} ms", updatedIdent, System.currentTimeMillis() - now));
    }

    private Mono<String> updatePersonInternal(String ident, PersonUpdateRequestDTO request, Boolean relaxed) {

        if (!isNumeric(ident) || ident.length() != 11) {

            return Mono.error(new InvalidRequestException(INVALID_IDENT));
        }

        return checkAlias(ident)
                .then(getDbPerson(ident))
                .flatMap(dbPerson -> mergeService.merge(request.getPerson(), dbPerson)
                        .flatMap(mergedPerson -> isNotTrue(relaxed) ?
                                validateArtifactsService.validate(mergedPerson.getPerson())
                                        .then(Mono.just(mergedPerson)) : Mono.just(mergedPerson))
                        .flatMap(mergedPerson -> personArtifactService.buildPerson(mergedPerson, relaxed))
                        .map(mergedPerson -> {
                            mergedPerson.setFornavn(mergedPerson.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO()).getFornavn());
                            mergedPerson.setMellomnavn(mergedPerson.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO()).getMellomnavn());
                            mergedPerson.setEtternavn(mergedPerson.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn());
                            mergedPerson.setSistOppdatert(now());
                            return mergedPerson;
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
                .flatMap(dbPerson -> unhookEksternePersonerService.unhook(dbPerson)
                        .then(Mono.just(dbPerson)))
                .flatMapMany(dbPerson -> Flux.concat(
                                Flux.just(dbPerson.getId()),
                                relasjonRepository.findByPersonId(dbPerson.getId())
                                        .map(DbRelasjon::getRelatertPersonId))
                        .flatMap(personRepository::findById)
                        .map(DbPerson::getIdent))
                .collect(Collectors.toCollection(HashSet::new))
                .flatMap(identerSomSkalSlettesHosPdl -> pdlTestdataConsumer.delete(identerSomSkalSlettesHosPdl)
                        .thenReturn(identerSomSkalSlettesHosPdl))
                .flatMap(identerSomSkalFriMarkeresIIdentpool ->
                        identPoolConsumer.releaseIdents(identerSomSkalFriMarkeresIIdentpool, Bruker.PDLF)
                                .thenReturn(identerSomSkalFriMarkeresIIdentpool))
                .flatMap(identerSomSkalSlettesIAliaser -> aliasRepository.deleteByIdentIn(identerSomSkalSlettesIAliaser)
                        .then(Mono.just(identerSomSkalSlettesIAliaser)))
                .flatMap(identerSomSkalSlettesIRelasjoner -> relasjonRepository.deleteByPersonIdentIn(identerSomSkalSlettesIRelasjoner)
                        .then(Mono.just(identerSomSkalSlettesIRelasjoner)))
                .flatMap(identerSomSkalSlettesIPdlForvalter -> personRepository.deleteByIdentIn(identerSomSkalSlettesIPdlForvalter)
                        .then(Mono.just(identerSomSkalSlettesIPdlForvalter)))
                .doOnNext(identer -> log.info("Sletting av ident {} tok {} ms", ident, currentTimeMillis() - startTime))
                .then();
    }

    @Transactional(readOnly = true)
    public Flux<FullPersonDTO> getPerson(List<String> identer) {

        val now = System.currentTimeMillis();

        if (nonNull(identer) && !identer.isEmpty()) {

            return aliasRepository.findByTidligereIdentIn(identer)
                    .collectList()
                    .flatMap(historikk -> Mono.zip(
                            Flux.fromIterable(historikk)
                                    .map(DbAlias::getPersonId)
                                    .flatMap(personRepository::findById)
                                    .map(DbPerson::getIdent)
                                    .collect(Collectors.toSet()),
                            Flux.fromIterable(historikk)
                                    .map(DbAlias::getTidligereIdent)
                                    .collect(Collectors.toSet())))
                    .map(oversikt -> {
                        val query = new HashSet<>(identer);
                        query.addAll(oversikt.getT1());
                        query.removeAll(oversikt.getT2());
                        return query;
                    })
                    .flatMapMany(personRepository::findByIdentInOrderBySistOppdatertDesc)
                    .collectList()
                    .flatMapMany(personer -> relasjonRepository.findByPersonIdIn(personer.stream()
                                    .map(DbPerson::getId)
                                    .toList())
                            .collectList()
                            .flatMapMany(relasjoner ->
                                    personRepository.findByIdIn(relasjoner.stream()
                                                    .map(DbRelasjon::getRelatertPersonId)
                                                    .collect(Collectors.toSet()))
                                            .collectMap(DbPerson::getId)
                                            .map(relatertePersoner -> {
                                                val context = MappingContextUtils.getMappingContext();
                                                context.setProperty("relasjoner", relasjoner);
                                                context.setProperty("relatertePersoner", relatertePersoner);
                                                return context;
                                            }))
                            .doOnNext(person -> log.info("Henting av personer tok {} ms", System.currentTimeMillis() - now))
                            .flatMap(context -> Flux.fromIterable(personer)
                                    .map(person -> mapperFacade.map(person, FullPersonDTO.class, context))));
        } else {
            return Flux.empty();
        }
    }

    @Transactional
    public Mono<String> createPerson(BestillingRequestDTO request) {

        val now = System.currentTimeMillis();

        if (isNull(request.getPerson())) {
            request.setPerson(new PersonDTO());
        }
        relasjonerAlderService.fixRelasjonerAlder(request);

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
                        .build(), null))
                .doOnNext(ident -> log.info("Oppretting av person {} tok {} ms", ident, System.currentTimeMillis() - now));
    }

    private Mono<IdentDTO> acquireIdentifier(BestillingRequestDTO request) {

        if (isBlank(request.getOpprettFraIdent())) {
            return Mono.just(mapperFacade.map(request, HentIdenterRequest.class))
                    .flatMap(identPoolConsumer::acquireIdent)
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
                .flatMap(eksisterendeIdent -> personRepository.findById(eksisterendeIdent.getPersonId()))
                .flatMap(dbPerson -> Mono.error(new InvalidRequestException(format(VIOLATION_ALIAS_EXISTS, dbPerson.getIdent()))))
                .then();
    }

    private Mono<DbPerson> getDbPerson(String ident) {

        return personRepository.findByIdent(ident)
                .switchIfEmpty(personRepository.save(DbPerson.builder()
                        .ident(ident)
                        .person(PersonDTO.builder()
                                .ident(ident)
                                .build())
                        .sistOppdatert(now())
                        .build()));
    }

    @Transactional
    public Mono<Void> deletePdlArtifacter(String ident) {

        return personRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(format("Ident %s ble ikke funnet", ident))))
                .flatMap(person -> hendelseIdService.deletePdlHendelser(person)
                        .then(unhookEksternePersonerService.unhook(person)
                                .then(relasjonRepository.deleteByPersonIdOrRelatertPersonId(person.getId())
                                        .thenReturn(person))))
                .then(personRepository.deleteByIdent(ident));
    }
}