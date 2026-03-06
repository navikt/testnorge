package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.IdentPoolConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.HentIdenterRequest;
import no.nav.pdl.forvalter.dto.IdentDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregistermetadataDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavPersonIdentifikatorDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master.FREG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master.PDL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.NPID;

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

    private static PersonDTO buildPerson(PersonRequestDTO request, IdentDTO identifikator) {

        return PersonDTO.builder()
                .kjoenn(List.of(KjoennDTO.builder()
                        .kjoenn(nonNull(request.getKjoenn()) ? request.getKjoenn() : null)
                        .folkeregistermetadata(new FolkeregistermetadataDTO())
                        .build()))
                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                        .foedselsdato(nonNull(identifikator.getFoedselsdato()) ?
                                identifikator.getFoedselsdato().atStartOfDay() : null)
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

    public Mono<DbPerson> execute(PersonRequestDTO request) {

        var startTime = currentTimeMillis();

        return identPoolConsumer.acquireIdent(
                        mapperFacade.map(nonNull(request) ? request : new PersonRequestDTO(), HentIdenterRequest.class))
                .doOnNext(identifikator -> Objects.requireNonNull(identifikator, "Kunne ikke hente ident fra identpool"))
                .flatMap(identifikator -> mergeService.merge(
                                buildPerson(nonNull(request) ? request : new PersonRequestDTO(), identifikator), new PersonDTO())
                        .doOnNext(mergedPerson -> mergedPerson.setIdent(identifikator.getIdent())))
                .flatMap(mergedPerson -> foedselsdatoService.convert(mergedPerson).thenReturn(mergedPerson))
                .flatMap(mergedPerson -> navnService.convert(mergedPerson).thenReturn(mergedPerson))
                .flatMap(mergedPerson -> bostedAdresseService.convert(mergedPerson, null).thenReturn(mergedPerson))
                .flatMap(mergedPerson -> foedestedService.convert(mergedPerson).thenReturn(mergedPerson))
                .flatMap(mergedPerson -> kjoennService.convert(mergedPerson).thenReturn(mergedPerson))
                .flatMap(mergedPerson -> statsborgerskapService.convert(mergedPerson).thenReturn(mergedPerson))
                .flatMap(mergedPerson -> adressebeskyttelseService.convert(mergedPerson).thenReturn(mergedPerson))
                .flatMap(mergedPerson -> navsPersonIdentifikatorService.convert(mergedPerson).thenReturn(mergedPerson))
                .flatMap(mergedPerson -> folkeregisterPersonstatusService.convert(mergedPerson).thenReturn(mergedPerson))
                .doOnNext(mergedPerson ->
                        mergedPerson.getSivilstand().add(SivilstandDTO.builder()
                                .type(SivilstandDTO.Sivilstand.UGIFT)
                                .isNew(true)
                                .id(1)
                                .master(request.getIdenttype() != NPID ? FREG : PDL)
                                .kilde("Dolly")
                                .bekreftelsesdato(request.getIdenttype() != NPID ? null : now())
                                .build()))
                .map(mergedPerson -> DbPerson.builder()
                        .person(mergedPerson)
                        .ident(mergedPerson.getIdent())
                        .fornavn(mergedPerson.getNavn().stream().findFirst().orElse(new NavnDTO()).getFornavn())
                        .mellomnavn(mergedPerson.getNavn().stream().findFirst().orElse(new NavnDTO()).getMellomnavn())
                        .etternavn(mergedPerson.getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn())
                        .sistOppdatert(now())
                        .build())
                .flatMap(personRepository::save)
                .doOnNext(person -> log.info("Oppretting av ident {} tok {} ms", person.getIdent(), currentTimeMillis() - startTime));
    }
}
