package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.testnav.libs.data.pdlforvalter.v1.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master.FREG;
import static no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master.PDL;
import static no.nav.testnav.libs.data.pdlforvalter.v1.Identtype.NPID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePersonService {

    private final IdentPoolConsumer identPoolConsumer;
    private final MapperFacade mapperFacade;
    private final MergeService mergeService;
    private final PersonRepository personRepository;
    private final KjoennService kjoennService;
    private final FoedselsdatoService foedselsdatoService;
    private final FoedestedService foedestedService;
    private final StatsborgerskapService statsborgerskapService;
    private final BostedAdresseService bostedAdresseService;
    private final NavnService navnService;
    private final AdressebeskyttelseService adressebeskyttelseService;
    private final NavPersonIdentifikatorService navsPersonIdentifikatorService;
    private final FolkeregisterPersonstatusService folkeregisterPersonstatusService;

    private static PersonDTO buildPerson(PersonRequestDTO request) {

        return PersonDTO.builder()
                .kjoenn(List.of(KjoennDTO.builder().kjoenn(request.getKjoenn())
                        .folkeregistermetadata(new FolkeregistermetadataDTO())
                        .build()))
                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                        .folkeregistermetadata(new FolkeregistermetadataDTO())
                        .build()))
                .foedested(List.of(FoedestedDTO.builder()
                        .folkeregistermetadata(new FolkeregistermetadataDTO())
                        .build()))
                .navn(List.of(nonNull(request.getNyttNavn()) ?
                        NavnDTO.builder().hasMellomnavn(request.getNyttNavn().isHasMellomnavn())
                                .folkeregistermetadata(new FolkeregistermetadataDTO())
                                .build() :
                        new NavnDTO()))
                .bostedsadresse(List.of(
                        BostedadresseDTO.builder()
                                .folkeregistermetadata(new FolkeregistermetadataDTO())
                                .build()))
                .statsborgerskap(List.of(StatsborgerskapDTO.builder()
                        .landkode(request.getStatsborgerskapLandkode())
                        .folkeregistermetadata(new FolkeregistermetadataDTO())
                        .build()))
                .adressebeskyttelse(nonNull(request.getGradering()) ?
                        List.of(AdressebeskyttelseDTO.builder()
                                .gradering(request.getGradering())
                                .folkeregistermetadata(new FolkeregistermetadataDTO())
                                .build()) : null)
                .navPersonIdentifikator(request.getIdenttype() == NPID ?
                        List.of(new NavPersonIdentifikatorDTO()) : null)
                .build();
    }

    public PersonDTO execute(PersonRequestDTO request) {

        var startTime = currentTimeMillis();

        var mergedPerson = mergeService.merge(buildPerson(nonNull(request) ? request : new PersonRequestDTO()),
                new PersonDTO());

        mergedPerson.setIdent(Objects.requireNonNull(identPoolConsumer.acquireIdents(
                                mapperFacade.map(nonNull(request) ? request : new PersonRequestDTO(), HentIdenterRequest.class))
                        .block())
                .getFirst()
                .getIdent());

        Stream.of(
                        Flux.just(foedselsdatoService.convert(mergedPerson)),
                        Flux.just(navnService.convert(mergedPerson)),
                        Flux.just(bostedAdresseService.convert(mergedPerson, null)),
                        Flux.just(foedestedService.convert(mergedPerson)),
                        Flux.just(kjoennService.convert(mergedPerson)),
                        Flux.just(statsborgerskapService.convert(mergedPerson)),
                        Flux.just(adressebeskyttelseService.convert(mergedPerson)),
                        Flux.just(navsPersonIdentifikatorService.convert(mergedPerson)),
                        Flux.just(folkeregisterPersonstatusService.convert(mergedPerson))
                )
                .reduce(Flux.empty(), Flux::merge)
                .collectList()
                .block();

        mergedPerson.getSivilstand().add(SivilstandDTO.builder()
                .type(SivilstandDTO.Sivilstand.UGIFT)
                .isNew(true)
                .id(1)
                .master(request.getIdenttype() != NPID ? FREG : PDL)
                .kilde("Dolly")
                .bekreftelsesdato(request.getIdenttype() != NPID ? null : now())
                .build());

        log.info("Oppretting av ident {} tok {} ms", mergedPerson.getIdent(), currentTimeMillis() - startTime);

        return personRepository.save(DbPerson.builder()
                        .person(mergedPerson)
                        .ident(mergedPerson.getIdent())
                        .fornavn(mergedPerson.getNavn().stream().findFirst().orElse(new NavnDTO()).getFornavn())
                        .mellomnavn(mergedPerson.getNavn().stream().findFirst().orElse(new NavnDTO()).getMellomnavn())
                        .etternavn(mergedPerson.getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn())
                        .sistOppdatert(now())
                        .build())
                .getPerson();
    }
}
