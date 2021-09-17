package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.consumer.PdlTestdataConsumer;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private static final String VIOLATION_ALIAS_EXISTS = "Utgått ident kan ikke endres. Benytt gjeldende ident %s for denne operasjonen";

    private final PersonRepository personRepository;
    private final MergeService mergeService;
    private final PersonArtifactService personArtifactService;
    private final MapperFacade mapperFacade;
    private final IdentPoolConsumer identPoolConsumer;
    private final PdlTestdataConsumer pdlTestdataConsumer;
    private final AliasRepository aliasRepository;
    private final ValidateArtifactsService validateArtifactsService;

    @Transactional
    public String updatePerson(String ident, PersonUpdateRequestDTO request) {

        checkAlias(ident);
        var dbPerson = personRepository.findByIdent(ident)
                .orElseGet(() -> personRepository.save(DbPerson.builder()
                        .ident(ident)
                        .person(PersonDTO.builder()
                                .ident(ident)
                                .build())
                        .sistOppdatert(now())
                        .build()));

        var mergedPerson = mergeService.merge(request.getPerson(), dbPerson.getPerson());
        validateArtifactsService.validate(mergedPerson);

        var extendedArtifacts = personArtifactService.buildPerson(mergedPerson);
        dbPerson.setPerson(extendedArtifacts);
        dbPerson.setSistOppdatert(now());

        return personRepository.save(dbPerson).getIdent();
    }

    @Transactional
    public void deletePerson(String ident) {

        var startTime = currentTimeMillis();

        checkAlias(ident);
        var dbPerson = personRepository.findByIdent(ident).orElseThrow(() ->
                new NotFoundException(format("Ident %s ble ikke funnet", ident)));

        var personer = Stream.of(List.of(dbPerson),
                        dbPerson.getRelasjoner().stream()
                                .map(DbRelasjon::getRelatertPerson)
                                .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var identer = personer.stream()
                .map(DbPerson::getIdent)
                .collect(Collectors.toList());

        Stream.of(
                        pdlTestdataConsumer.delete(identer),
                        identPoolConsumer.releaseIdents(identer),
                        Flux.just(personRepository.deleteByIdentIn(identer)))
                .reduce(Flux.empty(), Flux::merge)
                .collectList()
                .block();

        log.info("Sletting av ident {} tok {} ms", ident, currentTimeMillis() - startTime);
    }

    @Transactional(readOnly = true)
    public List<FullPersonDTO> getPerson(List<String> identer) {

        var query = new HashSet<>(identer);
        var aliaser = aliasRepository.findByTidligereIdentIn(identer);
        query.addAll(aliaser.stream()
                        .map(DbAlias::getPerson)
                .map(DbPerson::getIdent)
                        .collect(Collectors.toSet()));
        query.removeAll(aliaser.stream()
                .map(DbAlias::getTidligereIdent)
                .collect(Collectors.toSet()));

        return mapperFacade.mapAsList(personRepository.findByIdentIn(query), FullPersonDTO.class);
    }

    private void checkAlias(String ident) {

        var alias = aliasRepository.findByTidligereIdent(ident);
        if (alias.isPresent()) {
            throw new InvalidRequestException(
                    format(VIOLATION_ALIAS_EXISTS, alias.get().getPerson().getIdent()));
        }
    }

    public String createPerson(BestillingRequestDTO request) {

        if (isNull(request.getPerson())){
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

        return updatePerson(request.getPerson().getIdent(), PersonUpdateRequestDTO.builder()
                .person(request.getPerson())
                .build());
    }
}