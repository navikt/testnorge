package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.IdentTypeUtil.isTenorIdent;

@Service
@Slf4j
@RequiredArgsConstructor
public class NavigasjonService {

    private static final String IKKE_FUNNET = "%s ble ikke funnet i database";

    private final IdentRepository identRepository;
    private final IdentService identService;
    private final BestillingService bestillingService;
    private final MapperFacade mapperFacade;
    private final PersonServiceConsumer personServiceConsumer;
    private final PdlDataConsumer pdlDataConsumer;
    private final TestgruppeRepository testgruppeRepository;
    private final BestillingRepository bestillingRepository;
    private final BrukerService brukerService;
    private final BrukerRepository brukerRepository;

    @Transactional
    public Mono<RsWhereAmI> navigerTilIdent(String ident) {
        return brukerService.fetchOrCreateBruker()
                .flatMapMany(bruker -> Flux.merge(
                                getPdlForvalterIdenter(ident),
                                getPdlPersonIdenter(ident))
                        .filter(ident1 -> filterOnBrukertype(ident, bruker.getBrukertype()))
                        .distinct()
                        .flatMap(ident1 -> identRepository.findByIdent(ident1)
                                .flatMap(testident -> Mono.zip(testgruppeRepository.findById(testident.getGruppeId()),
                                                identService.getPaginertIdentIndex(testident.getIdent(), testident.getGruppeId()),
                                                identRepository.countByGruppeId(testident.getGruppeId()),
                                                bestillingRepository.countByGruppeId(testident.getGruppeId()),
                                                identRepository.countByGruppeIdAndIBruk(testident.getGruppeId(), true),
                                                brukerRepository.findAll()
                                                        .reduce(new HashMap<Long, Bruker>(), (map, bruker1) -> {
                                                            map.put(bruker1.getId(), bruker1);
                                                            return map;
                                                        }))
                                        .map(tuple -> {
                                            var context = MappingContextUtils.getMappingContext();
                                            context.setProperty("bruker", bruker);
                                            context.setProperty("antallIdenter", tuple.getT3());
                                            context.setProperty("antallBestillinger", tuple.getT4());
                                            context.setProperty("antallIBruk", tuple.getT5());
                                            context.setProperty("alleBrukere", tuple.getT6());
                                            return RsWhereAmI.builder()
                                                    .gruppe(mapperFacade.map(tuple.getT1(), RsTestgruppe.class, context))
                                                    .identHovedperson(testident.getIdent())
                                                    .identNavigerTil(ident)
                                                    .sidetall(Math.floorDiv(tuple.getT2(), 10))
                                                    .build();
                                        }))))
                .collectList()
                .flatMap(list -> list.isEmpty() ?
                        Mono.error(new NotFoundException(String.format(IKKE_FUNNET, ident))) :
                        Mono.just(list.getFirst()));
    }

    public Mono<RsWhereAmI> navigerTilBestilling(Long bestillingId) {

        return bestillingService.fetchBestillingById(bestillingId)
                .switchIfEmpty(Mono.error(() -> new NotFoundException(String.format(IKKE_FUNNET, bestillingId))))
                .flatMap(bestilling -> Mono.zip(
                        testgruppeRepository.findByBestillingId(bestilling.getId()),
                        bestillingService.getPaginertBestillingIndex(bestillingId, bestilling.getGruppeId()),
                        brukerService.fetchOrCreateBruker(),
                        identRepository.countByGruppeId(bestilling.getGruppeId()),
                        bestillingRepository.countByGruppeId(bestilling.getGruppeId()),
                        identRepository.countByGruppeIdAndIBruk(bestilling.getGruppeId(), true),
                        brukerRepository.findAll()
                                .reduce(new HashMap<Long, Bruker>(), (map, bruker1) -> {
                                    map.put(bruker1.getId(), bruker1);
                                    return map;
                                })))
                .map(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("bruker", tuple.getT3());
                    context.setProperty("antallIdenter", tuple.getT4());
                    context.setProperty("antallBestillinger", tuple.getT5());
                    context.setProperty("antallIBruk", tuple.getT6());
                    context.setProperty("alleBrukere", tuple.getT7());
                    return RsWhereAmI.builder()
                            .bestillingNavigerTil(bestillingId)
                            .gruppe(mapperFacade.map(tuple.getT1(), RsTestgruppe.class, context))
                            .sidetall(Math.floorDiv(tuple.getT2(), 10))
                            .build();
                });
    }

    private boolean filterOnBrukertype(String ident, Bruker.Brukertype brukertype) {

        if (brukertype == Bruker.Brukertype.BANKID) {
            return isTenorIdent(ident);
        }
        return true;
    }

    private Flux<String> getPdlPersonIdenter(String ident) {

        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .flatMap(personBolk -> Flux.fromStream(Stream.of(Stream.of(ident),
                                personBolk.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull),
                                personBolk.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .filter(Objects::nonNull),
                                personBolk.getPerson().getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig)
                                        .filter(Objects::nonNull),
                                personBolk.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident)
                                        .filter(Objects::nonNull),
                                personBolk.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                        .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                        .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                        .filter(Objects::nonNull),
                                personBolk.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                        .filter(Objects::nonNull)
                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                        .filter(Objects::nonNull))
                        .flatMap(Function.identity())));
    }

    private Flux<String> getPdlForvalterIdenter(String ident) {

        return pdlDataConsumer.getPersoner(List.of(ident))
                .flatMap(person -> Flux.fromStream(Stream.of(Stream.of(ident),
                                person.getPerson().getSivilstand().stream()
                                        .map(SivilstandDTO::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                                        .filter(Objects::nonNull),
                                person.getPerson().getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig)
                                        .filter(Objects::nonNull),
                                person.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident)
                                        .filter(Objects::nonNull),
                                person.getRelasjoner().stream()
                                        .filter(relasjon -> relasjon.getRelasjonType() == RelasjonType.FULLMEKTIG ||
                                                relasjon.getRelasjonType() == RelasjonType.FULLMAKTSGIVER)
                                        .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                                        .map(PersonDTO::getIdent),
                                person.getPerson().getVergemaal().stream()
                                        .map(VergemaalDTO::getVergeIdent)
                                        .filter(Objects::nonNull),
                                person.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                        .filter(Objects::nonNull)
                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                        .filter(Objects::nonNull))
                        .flatMap(Function.identity())));
    }
}
