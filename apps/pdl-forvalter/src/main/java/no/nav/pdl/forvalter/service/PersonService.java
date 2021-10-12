package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO.PersonIDDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private static final String INVALID_IDENT = "Ident må være på 11 tegn og numerisk";
    private static final String EMPTY_GET_REQUEST = "Angi en av parametrene 'identer' eller 'fragment'";
    private static final String VIOLATION_ALIAS_EXISTS = "Utgått ident kan ikke endres. Benytt gjeldende ident %s for denne operasjonen";

    private final PersonRepository personRepository;
    private final MergeService mergeService;
    private final PersonArtifactService personArtifactService;
    private final MapperFacade mapperFacade;
    private final IdentPoolConsumer identPoolConsumer;
    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final AliasRepository aliasRepository;
    private final ValidateArtifactsService validateArtifactsService;
    private final RelasjonRepository relasjonRepository;

    @Transactional
    public String updatePerson(String ident, PersonUpdateRequestDTO request) {

        if (!isNumeric(ident) || ident.length() != 11) {

            throw new InvalidRequestException(INVALID_IDENT);
        }

        checkAlias(ident);
        var dbPerson = personRepository.findByIdent(ident)
                .switchIfEmpty(Mono.defer(() -> personRepository.save(DbPerson.builder()
                        .ident(ident)
                        .person(PersonDTO.builder()
                                .ident(ident)
                                .build())
                        .sistOppdatert(now())
                        .build()))).block();

        var mergedPerson = mergeService.merge(request.getPerson(), dbPerson.getPerson());
        validateArtifactsService.validate(mergedPerson);

        var extendedArtifacts = personArtifactService.buildPerson(mergedPerson);
        dbPerson.setPerson(extendedArtifacts);
        dbPerson.setFornavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getFornavn());
        dbPerson.setMellomnavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getMellomnavn());
        dbPerson.setEtternavn(extendedArtifacts.getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn());
        dbPerson.setSistOppdatert(now());

        return personRepository.save(dbPerson).block().getIdent();
    }

    @Transactional
    public void deletePerson(String ident) {

        var startTime = currentTimeMillis();

        checkAlias(ident);
        var dbPerson = personRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(() ->
                        new NotFoundException(format("Ident %s ble ikke funnet", ident))))
                .flatMap(person -> Mono.zip(
                        relasjonRepository.findByPersonId(person.getId())
                                .flatMap(relasjon -> Mono.zip(
                                        personRepository.findById(relasjon.getRelatertPersonId()),
                                        Mono.empty(),
                                        (relatertPerson, empty) -> {
                                            relasjon.setRelatertPerson(relatertPerson);
                                            return relasjon;
                                        }))
                                .collectList(),
                        aliasRepository.findByPersonId(person.getId())
                                .collectList(),
                        (relasjoner, aliaser) -> {
                            person.setRelasjoner(relasjoner);
                            person.setAlias(aliaser);
                            return person;
                        }))
                .map(person ->
                        Stream.of(List.of(person.getIdent()),
                                        person.getRelasjoner().stream()
                                                .map(DbRelasjon::getRelatertPerson)
                                                .map(DbPerson::getIdent)
                                                .collect(Collectors.toList()))
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()))
                .map(identer ->
                        Stream.of(
                                        pdlTestdataConsumer.delete(identer),
                                        identPoolConsumer.releaseIdents(identer),
                                        Flux.just(personRepository.deleteByIdentIn(identer)))
                                .reduce(Flux.empty(), Flux::merge)
                                .collectList()
                                .block());

        log.info("Sletting av ident {} tok {} ms", ident, currentTimeMillis() - startTime);
    }

    @Transactional(readOnly = true)
    public Flux<FullPersonDTO> getPerson(List<String> identer, String fragment) {

        if (nonNull(identer) && !identer.isEmpty()) {

            return Flux.concat(
                            aliasRepository.findByTidligereIdentIn(identer)
                                    .flatMap(alias -> personRepository.findById(alias.getPersonId())),
                            personRepository.findByIdentIn(identer))
                    .flatMap(person -> Mono.zip(relasjonRepository.findByPersonId(person.getId()).collectList(),
                            Mono.empty(),
                            (relasjoner, empty) -> {
                                person.setRelasjoner(relasjoner);
                                return person;
                            }))
                    .map(person -> mapperFacade.map(person, FullPersonDTO.class));

        } else if (isNotBlank(fragment)) {

            return searchPerson(fragment)
                    .map(person -> FullPersonDTO.builder()
                            .identitet(PersonIDDTO.builder()
                                    .ident(person.getIdent())
                                    .fornavn(person.getFornavn())
                                    .mellomnavn(person.getMellomnavn())
                                    .etternavn(person.getEtternavn())
                                    .build())
                            .build());

        } else {

            throw new InvalidRequestException(EMPTY_GET_REQUEST);
        }
    }

    public String createPerson(BestillingRequestDTO request) {

        if (isNull(request.getPerson())) {
            request.setPerson(new PersonDTO());
        }

        request.getPerson().setIdent(identPoolConsumer.getIdents(
                        mapperFacade.map(request, HentIdenterRequest.class))
                .blockFirst().stream().findFirst().get().getIdent());

        if (request.getPerson().getKjoenn().isEmpty()) {
            request.getPerson().getKjoenn().add(new KjoennDTO());
        }
        if (request.getPerson().getFoedsel().isEmpty()) {
            request.getPerson().getFoedsel().add(new FoedselDTO());
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
        if (request.getPerson().getFolkeregisterpersonstatus().isEmpty()) {
            request.getPerson().getFolkeregisterpersonstatus().add(new FolkeregisterpersonstatusDTO());
        }

        return updatePerson(request.getPerson().getIdent(), PersonUpdateRequestDTO.builder()
                .person(request.getPerson())
                .build());
    }

    private void checkAlias(String ident) {

        aliasRepository.findByTidligereIdent(ident)
                .map(alias -> {
                    if (nonNull(alias)) {
                        throw new InvalidRequestException(
                                format(VIOLATION_ALIAS_EXISTS, alias.getTidligereIdent()));
                    }
                    return null;
                });
    }

    private Flux<DbPerson> searchPerson(String query) {
        Optional<String> ident = Stream.of(query.split(" "))
                .filter(StringUtils::isNumeric)
                .findFirst();

        List<String> navn = List.of(query.split(" ")).stream()
                .filter(fragment -> isNotBlank(fragment) && !StringUtils.isNumeric(fragment))
                .collect(Collectors.toList());

        return personRepository.findByWildcardIdent(ident.orElse(null),
                !navn.isEmpty() ? navn.get(0).toUpperCase() : null,
                navn.size() > 1 ? navn.get(1).toUpperCase() : null,
                PageRequest.of(0, 10));
    }
}