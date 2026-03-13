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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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

    private static final String SORT_BY_FIELD = "sistOppdatert";

    private final AliasRepository aliasRepository;
    private final HendelseIdService hendelseIdService;
    private final IdentPoolConsumer identPoolConsumer;
    private final MapperFacade mapperFacade;
    private final MergeService mergeService;
    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final PersonArtifactService personArtifactService;
    private final PersonRepository personRepository;
    private final RelasjonRepository relasjonRepository;
    private final UnhookEksternePersonerService unhookEksternePersonerService;
    private final ValidateArtifactsService validateArtifactsService;

    @Transactional
    public Mono<String> updatePerson(String ident, PersonUpdateRequestDTO request, Boolean relaxed) {

        return updatePersonInternal(ident, request, relaxed);
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
                        .thenReturn(dbPerson))
                .flatMapMany(dbPerson -> Flux.fromIterable(dbPerson.getRelasjoner())
                        .map(DbRelasjon::getRelatertPerson)
                                // Identer som har blitt merget DNR/FNR <-> NPID kan ikke gjenbrukes da disse har blitt koblet permanent i PDL-aktoer
                                .flatMap(relatertPerson -> aliasRepository.existsByTidligereIdent(relatertPerson.getIdent())
                                        .zipWith(Mono.just(relatertPerson))))
                        .filter(tuple -> isNotTrue(tuple.getT1()))
                .reduce(new HashSet<String>(), (set, tuple) -> {
                    set.add(tuple.getT2().getIdent());
                    return set;
                })
                .flatMap(identerSomSkalSlettesHosPdl -> pdlTestdataConsumer.delete(identerSomSkalSlettesHosPdl)
                        .thenReturn(identerSomSkalSlettesHosPdl))
                .flatMap(identerSomSkalFriMarkeresIIdentpool ->
                        identPoolConsumer.releaseIdents(identerSomSkalFriMarkeresIIdentpool, Bruker.PDLF)
                                .thenReturn(identerSomSkalFriMarkeresIIdentpool))
                .flatMap(identerSomSkalSlettesIRelasjoner -> relasjonRepository.deleteByPersonIdentIn(identerSomSkalSlettesIRelasjoner)
                        .thenReturn(identerSomSkalSlettesIRelasjoner))
                .flatMap(identerSomSkalSlettesIPdlForvalter -> personRepository.deleteByIdentIn(identerSomSkalSlettesIPdlForvalter)
                        .thenReturn(identerSomSkalSlettesIPdlForvalter))
                .doOnNext(identer -> log.info("Sletting av ident {} tok {} ms", ident, currentTimeMillis() - startTime))
                .then();
    }

    @Transactional(readOnly = true)
    public Flux<FullPersonDTO> getPerson(List<String> identer, Paginering paginering) {

        val now = System.currentTimeMillis();

        if (nonNull(identer) && !identer.isEmpty()) {

            return aliasRepository.findByTidligereIdentIn(identer)
                    .map(DbAlias::getPerson)
                    .map(DbPerson::getIdent)
                    .reduce(new HashSet<String>(), (set, ident) -> {
                        set.add(ident);
                        return set;
                    })
                    .map(identerFraAlias -> {
                        val identerSet = new HashSet<>(identer);
                        identerSet.removeAll(identerFraAlias);
                        return identerSet;
                    })
                    .flatMapMany(query -> personRepository.findByIdentIn(query,
                                    Pageable.unpaged()))
                    .map(person -> mapperFacade.map(person, FullPersonDTO.class))
                    .doOnNext(person -> log.info("Henting av person med ident {} tok {} ms",
                            person.getPerson().getIdent(), System.currentTimeMillis() - now));

        } else {
            return Flux.empty();
//            return personRepository.findAll(
//                            PageRequest.of(paginering.getSidenummer(),
//                                    paginering.getSidestoerrelse(),
//                                    Sort.by(SORT_BY_FIELD).descending()))
//                    .map(person -> mapperFacade.map(person, FullPersonDTO.class)); //TBD
        }
    }

    @Transactional
    public Mono<String> createPerson(BestillingRequestDTO request) {

        val now = System.currentTimeMillis();

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
                        .build(), null))
                .doOnNext(ident -> log.info("Oppretting av person {} tok {} ms", ident, System.currentTimeMillis() - now));
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