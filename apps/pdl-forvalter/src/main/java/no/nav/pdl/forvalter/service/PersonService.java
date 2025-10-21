package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import no.nav.testnav.libs.data.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavPersonIdentifikatorDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.getIdenttype;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.isNotNpidIdent;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.isNpidIdent;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.GAMMEL_IDENTITET;
import static no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType.NY_IDENTITET;
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
    private final RelasjonerAlderService relasjonerAlderService;
    private final HendelseIdService hendelseIdService;

    @Transactional
    public String updatePerson(String ident, PersonUpdateRequestDTO request, Boolean overwrite, Boolean relaxed) {

        if (!isNumeric(ident) || ident.length() != 11) {

            throw new InvalidRequestException(INVALID_IDENT);
        }

        checkAlias(ident);
        var dbPerson = getDbPerson(ident, overwrite);

        var mergedPerson = mergeService.merge(request.getPerson(), dbPerson.getPerson());
        if (isNotTrue(relaxed)) {
            validateArtifactsService.validate(mergedPerson);
        }

        var extendedArtifacts = personArtifactService.buildPerson(mergedPerson, relaxed);
        dbPerson.setPerson(extendedArtifacts);
        dbPerson.setFornavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getFornavn());
        dbPerson.setMellomnavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getMellomnavn());
        dbPerson.setEtternavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn());
        dbPerson.setSistOppdatert(now());

        return personRepository.save(dbPerson).getIdent();
    }

    @Transactional
    public void deletePerson(String ident) {

        var startTime = currentTimeMillis();

        checkAlias(ident);
        var dbPerson = personRepository.findByIdent(ident).orElseThrow(() ->
                new NotFoundException(format("Ident %s ble ikke funnet", ident)));

        // Identer som har blitt merget DNR/FNR <-> NPID kan ikke gjenbrukes da disse har blitt koblet permanent i PDL-aktoer
        var identerSomIkkeSkalSlettesFraIdentpool = new ArrayList<>(dbPerson.getRelasjoner().stream()
                .filter(relasjon -> (relasjon.getRelasjonType() == NY_IDENTITET || relasjon.getRelasjonType() == GAMMEL_IDENTITET))
                .map(DbRelasjon::getRelatertPerson)
                .map(DbPerson::getIdent)
                .filter(id -> isNpidIdent(id) && isNotNpidIdent(dbPerson.getIdent()) ||
                        isNotNpidIdent(id) && isNpidIdent(dbPerson.getIdent()))
                .toList());

        if (!identerSomIkkeSkalSlettesFraIdentpool.isEmpty()) {
            identerSomIkkeSkalSlettesFraIdentpool.add(dbPerson.getIdent());
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

    @Transactional(readOnly = true)
    public List<FullPersonDTO> getPerson(List<String> identer, Paginering paginering) {

        if (nonNull(identer) && !identer.isEmpty()) {
            var query = new HashSet<>(identer);
            var aliaser = aliasRepository.findByTidligereIdentIn(identer);
            query.addAll(aliaser.stream()
                    .map(DbAlias::getPerson)
                    .map(DbPerson::getIdent)
                    .collect(Collectors.toSet()));
            query.removeAll(aliaser.stream()
                    .map(DbAlias::getTidligereIdent)
                    .collect(Collectors.toSet()));

            return mapperFacade.mapAsList(personRepository.findByIdentIn(query,
                            PageRequest.of(paginering.getSidenummer(),
                                    paginering.getSidestoerrelse(),
                                    Sort.by(SORT_BY_FIELD).descending())),
                    FullPersonDTO.class);

        } else {

            return mapperFacade.mapAsList(personRepository.findAll(
                            PageRequest.of(paginering.getSidenummer(),
                                    paginering.getSidestoerrelse(),
                                    Sort.by(SORT_BY_FIELD).descending())),
                    FullPersonDTO.class);
        }
    }

    public String createPerson(BestillingRequestDTO request) {

        if (isNull(request.getPerson())) {
            request.setPerson(new PersonDTO());
        }
        relasjonerAlderService.fixRelasjonerAlder(request);

        IdentDTO identifier = null;
        if (isBlank(request.getOpprettFraIdent())) {
            identifier = identPoolConsumer.acquireIdents(
                    mapperFacade.map(request, HentIdenterRequest.class)).block();
            assert identifier != null;
            request.getPerson().setIdent(identifier.getIdent());

        } else {
            if (personRepository.existsByIdent(request.getOpprettFraIdent())) {
                throw new InvalidRequestException(format(IDENT_ALREADY_EXISTS, request.getOpprettFraIdent()));
            }
            identPoolConsumer.allokerIdent(request.getOpprettFraIdent()).block();
            request.getPerson().setIdent(request.getOpprettFraIdent());
        }

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

        return updatePerson(request.getPerson().getIdent(), PersonUpdateRequestDTO.builder()
                .person(request.getPerson())
                .build(), null, null);
    }

    private void checkAlias(String ident) {

        var alias = aliasRepository.findByTidligereIdent(ident);
        if (alias.isPresent()) {
            throw new InvalidRequestException(
                    format(VIOLATION_ALIAS_EXISTS, alias.get().getPerson().getIdent()));
        }
    }

    private DbPerson getDbPerson(String ident, Boolean overwrite) {

        if (isTrue(overwrite)) {
            personRepository.deleteByIdent(ident);
        }
        return personRepository.findByIdent(ident)
                .orElseGet(() -> personRepository.save(DbPerson.builder()
                        .ident(ident)
                        .person(PersonDTO.builder()
                                .ident(ident)
                                .build())
                        .sistOppdatert(now())
                        .build()));
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