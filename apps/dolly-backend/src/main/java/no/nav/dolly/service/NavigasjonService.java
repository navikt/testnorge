package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class NavigasjonService {

    private static final String IKKE_FUNNET = "%s ble ikke funnet i database";

    private final IdentRepository identRepository;
    private final IdentService identService;
    private final BestillingService bestillingService;
    private final MapperFacade mapperFacade;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final PdlDataConsumer pdlDataConsumer;

    @Transactional(readOnly = true)
    public Mono<RsWhereAmI> navigerTilIdent(String ident) {

        return Flux.merge(getPdlForvalterIdenter(ident),
                        getPdlPersonIdenter(ident))
                .distinct()
                .flatMap(ident1 -> Mono.just(identRepository.findByIdent(ident1))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(testident -> RsWhereAmI.builder()
                                .gruppe(mapperFacade.map(testident.getTestgruppe(), RsTestgruppe.class))
                                .identHovedperson(testident.getIdent())
                                .identNavigerTil(ident)
                                .sidetall(Math.floorDiv(
                                        identService.getPaginertIdentIndex(testident.getIdent(), testident.getTestgruppe().getId())
                                                .orElseThrow(() -> new NotFoundException(String.format(IKKE_FUNNET, ident))), 10))
                                .build()))
                .switchIfEmpty(Flux.error(() -> new NotFoundException(String.format(IKKE_FUNNET, ident))))
                .next();
    }

    public Mono<RsWhereAmI> navigerTilBestilling(Long bestillingId) {

        return Mono.just(bestillingService.fetchBestillingById(bestillingId))
                .map(bestilling -> RsWhereAmI.builder()
                        .bestillingNavigerTil(bestillingId)
                        .gruppe(mapperFacade.map(bestilling.getGruppe(), RsTestgruppe.class))
                        .sidetall(Math.floorDiv(
                                bestillingService.getPaginertBestillingIndex(bestillingId, bestilling.getGruppe().getId())
                                        .orElseThrow(() -> new NotFoundException(String.format(IKKE_FUNNET, bestillingId))), 10))
                        .build())
                .switchIfEmpty(Mono.error(() -> new NotFoundException(String.format(IKKE_FUNNET, bestillingId))));
    }

    private Flux<String> getPdlPersonIdenter(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .flatMap(personBolk -> Flux.fromStream(Stream.of(Stream.of(ident),
                                personBolk.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand),
                                personBolk.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent),
                                personBolk.getPerson().getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig),
                                personBolk.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident),
                                personBolk.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                        .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                        .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident),
                                personBolk.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                        .filter(Objects::nonNull)
                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer))
                        .filter(Objects::nonNull)
                        .flatMap(Function.identity())));
    }

    private Flux<String> getPdlForvalterIdenter(String ident) {

        return pdlDataConsumer.getPersoner(List.of(ident))
                .flatMap(person -> Flux.fromStream(Stream.of(Stream.of(ident),
                                person.getPerson().getSivilstand().stream()
                                        .map(SivilstandDTO::getRelatertVedSivilstand),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(ForelderBarnRelasjonDTO::getRelatertPerson),
                                person.getPerson().getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig),
                                person.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident),
                                person.getPerson().getVergemaal().stream()
                                        .map(VergemaalDTO::getVergeIdent),
                                person.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                        .filter(Objects::nonNull)
                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer))
                        .filter(Objects::nonNull)
                        .flatMap(Function.identity())));
    }
}
