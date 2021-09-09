package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.dto.IdentDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class CreatePersonService {

    private final IdentPoolConsumer identPoolConsumer;
    private final MapperFacade mapperFacade;
    private final MergeService mergeService;
    private final PersonRepository personRepository;
    private final KjoennService kjoennService;
    private final FoedselService foedselService;
    private final StatsborgerskapService statsborgerskapService;
    private final BostedAdresseService bostedAdresseService;
    private final NavnService navnService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;

    private static PersonDTO buildPerson(PersonRequestDTO request) {

        return PersonDTO.builder()
                .kjoenn(List.of(KjoennDTO.builder().kjoenn(request.getKjoenn()).build()))
                .foedsel(List.of(FoedselDTO.builder().build()))
                .navn(List.of(nonNull(request.getNyttNavn()) ?
                        NavnDTO.builder().hasMellomnavn(request.getNyttNavn().isHarMellomnavn()).build() :
                        new NavnDTO()))
                .bostedsadresse(List.of(
                        BostedadresseDTO.builder()
                                .vegadresse(new VegadresseDTO())
                                .build()))
                .statsborgerskap(List.of(StatsborgerskapDTO.builder().build()))
                .folkeregisterpersonstatus(
                        List.of(FolkeregisterpersonstatusDTO.builder().build()))
                .build();
    }

    public PersonDTO execute(PersonRequestDTO request) {

        var mergedPerson = mergeService.merge(buildPerson(nonNull(request) ? request : new PersonRequestDTO()),
                new PersonDTO());

        var delivery = Stream.of(
                        Flux.just(Arrays.stream(identPoolConsumer.getIdents(
                                        mapperFacade.map(nonNull(request) ? request : new PersonRequestDTO(), HentIdenterRequest.class)))
                                .map(ident -> IdentDTO.builder()
                                        .ident(ident)
                                        .build())
                                .collect(Collectors.toList())),
                        Flux.just(navnService.convert(mergedPerson.getNavn())))
                .reduce(Flux.empty(), Flux::merge)
                .collectList()
                .block();

        mergedPerson.setIdent(delivery.stream()
                .filter(list -> list.stream().anyMatch(item -> item instanceof IdentDTO))
                .flatMap(Collection::stream)
                .map(IdentDTO.class::cast)
                .findFirst().get().getIdent());

        Stream.of(
                        Flux.just(bostedAdresseService.convert(mergedPerson)),
                        Flux.just(kjoennService.convert(mergedPerson)),
                        Flux.just(statsborgerskapService.convert(mergedPerson)),
                        Flux.just(foedselService.convert(mergedPerson)))
                .reduce(Flux.empty(), Flux::merge)
                .collectList()
                .block();

        folkeregisterPersonstatusService.convert(mergedPerson);

        return personRepository.save(DbPerson.builder()
                        .person(mergedPerson)
                        .ident(mergedPerson.getIdent())
                        .sistOppdatert(LocalDateTime.now())
                        .build())
                .getPerson();
    }
}
