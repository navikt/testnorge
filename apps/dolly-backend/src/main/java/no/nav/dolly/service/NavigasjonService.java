package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class NavigasjonService {

    private final IdentRepository identRepository;
    private final IdentService identService;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;
    private final PdlDataConsumer pdlDataConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final ExecutorService dollyForkJoinPool;

    public RsWhereAmI navigerTilIdent(String ident) {

        var miljoIdenter = Stream.of(List.of(ident),
                        tpsfService.hentTestpersoner(List.of(ident)).stream().findFirst().orElse(new Person())
                                .getRelasjoner().stream()
                                .map(Relasjon::getPersonRelasjonMed)
                                .map(Person::getIdent)
                                .toList(),
                        pdlDataConsumer.getPersoner(List.of(ident)).stream().findFirst().orElse(new FullPersonDTO())
                                .getRelasjoner().stream()
                                .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                .map(PersonDTO::getIdent)
                                .toList(),
                        getPdlPersoner(ident))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        var testident = identRepository.findByIdentIn(miljoIdenter).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(ident + " ble ikke funnet i database"));

        return RsWhereAmI.builder()
                .gruppe(mapperFacade.map(testident.getTestgruppe(), RsTestgruppe.class))
                .identHovedperson(testident.getIdent())
                .identNavigerTil(ident)
                .sidetall(Math.floorDiv(
                        identService.getPaginertIdentIndex(testident.getIdent(), testident.getTestgruppe().getId())
                                .orElseThrow(() -> new NotFoundException(ident + " ble ikke funnet i database")), 10))
                .build();
    }

    private List<String> getPdlPersoner(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .map(PdlPersonBolk.PersonBolk::getPerson)
                .map(person -> Stream.of(List.of(ident),
                                person.getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .toList(),
                                person.getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getVergemaalEllerFremtidsfullmakt().stream()
                                        .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                        .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getKontaktinformasjonForDoedsbo().stream()
                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                        .filter(Objects::nonNull)
                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                        .filter(Objects::nonNull)
                                        .toList())
                        .flatMap(Collection::stream)
                        .distinct()
                        .toList())
                .flatMap(Flux::fromIterable)
                .collectList()
                .block();
    }
}
