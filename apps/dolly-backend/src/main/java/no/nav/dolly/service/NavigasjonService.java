package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class NavigasjonService {

    private final IdentRepository identRepository;
    private final IdentService identService;
    private final TpsfService tpsfService;
    private final MapperFacade mapperFacade;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final ExecutorService dollyForkJoinPool;

    @SneakyThrows
    public RsWhereAmI navigerTilIdent(String ident) {

        var tpsfStatus = CompletableFuture.supplyAsync(
                () -> getTpsfIdenter(ident), dollyForkJoinPool);
        var pdlStatus = CompletableFuture.supplyAsync(
                () -> getPdlIdenter(ident), dollyForkJoinPool);

        var miljoeIdenter = CompletableFuture.allOf(tpsfStatus, pdlStatus)

                .thenApply(result -> Stream.of(tpsfStatus, pdlStatus)
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .toList())
                .get();

        var testident = identRepository.findByIdentIn(miljoeIdenter).stream().findFirst()
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

    private List<String> getTpsfIdenter(String ident) {

        return tpsfService.hentTestpersoner(List.of(ident)).stream()
                .map(Person::getRelasjoner)
                .flatMap(Collection::stream)
                .map(Relasjon::getPersonRelasjonMed)
                .map(Person::getIdent)
                .toList();
    }

    private List<String> getPdlIdenter(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .map(personBolk -> Stream.of(List.of(ident),
                                personBolk.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                personBolk.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .toList(),
                                personBolk.getPerson().getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                personBolk.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                personBolk.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                        .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                        .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                personBolk.getPerson().getKontaktinformasjonForDoedsbo().stream()
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
