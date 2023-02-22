package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private static final int PAGE_SIZE = 100;

    private final List<ClientRegister> clientRegister;
    private final PdlDataConsumer pdlDataConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final BestillingService bestillingService;
    private final IdentService identService;

    @Async
    @Transactional
    public void recyclePersoner(List<TestidentDTO> testidenter) {

        if (testidenter.stream().anyMatch(TestidentDTO::isPdlf)) {

            var pdlfIdenter = testidenter.stream()
                    .filter(TestidentDTO::isPdlf)
                    .map(TestidentDTO::getIdent)
                    .toList();

            var pdlfRelasjoner = Flux.range(0, (pdlfIdenter.size() / PAGE_SIZE) + 1)
                    .flatMap(index -> pdlDataConsumer.getPersoner(pdlfIdenter.subList(index * PAGE_SIZE,
                            Math.min((index + 1) * PAGE_SIZE, pdlfIdenter.size())), 0, PAGE_SIZE))
                    .map(person -> Stream.of(
                                    person.getPerson().getSivilstand().stream()
                                            .filter(sivilstand -> !sivilstand.isEksisterendePerson())
                                            .map(SivilstandDTO::getRelatertVedSivilstand)
                                            .toList(),
                                    person.getPerson().getForelderBarnRelasjon().stream()
                                            .filter(relasjon -> !relasjon.isEksisterendePerson())
                                            .map(ForelderBarnRelasjonDTO::getRelatertPerson)
                                            .toList(),
                                    person.getPerson().getForeldreansvar().stream()
                                            .filter(ansvar -> !ansvar.isEksisterendePerson())
                                            .map(ForeldreansvarDTO::getAnsvarlig)
                                            .toList(),
                                    person.getPerson().getVergemaal().stream()
                                            .filter(vergemaal -> !vergemaal.isEksisterendePerson())
                                            .map(VergemaalDTO::getVergeIdent)
                                            .toList(),
                                    person.getPerson().getFullmakt().stream()
                                            .filter(fullmakt -> !fullmakt.isEksisterendePerson())
                                            .map(FullmaktDTO::getMotpartsPersonident)
                                            .toList(),
                                    person.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                            .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                            .filter(Objects::nonNull)
                                            .filter(personKontakt -> !personKontakt.isEksisterendePerson())
                                            .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                            .toList())
                            .flatMap(Collection::stream)
                            .filter(Objects::nonNull)
                            .toList())
                    .flatMap(Flux::fromIterable)
                    .distinct()
                    .collectList()
                    .block();

            pdlDataConsumer.slettPdl(pdlfIdenter.stream()
                            .filter(ident -> pdlfRelasjoner.stream().noneMatch(relasjon -> relasjon.equals(ident)))
                            .toList())
                    .subscribe(response -> log.info("Slettet antall {} identer mot PDL-forvalter", pdlfIdenter.size()));
        }

        if (testidenter.stream().anyMatch(TestidentDTO::isTpsf)) {

            pdlDataConsumer.slettPdl(testidenter.stream()
                            .filter(TestidentDTO::isTpsf)
                            .map(TestidentDTO::getIdent)
                            .toList())
                    .subscribe(response -> log.info("Slettet TPSF-ident {} mot PDL-forvalter", testidenter.get(0).getIdent()));
        }

        slettTestnorgeRelasjonerIntern(testidenter);
        releaseArtifacts(testidenter);
    }

    @Transactional
    public void slettTestnorgeRelasjoner(List<TestidentDTO> testidenter) {

        slettTestnorgeRelasjonerIntern(testidenter);
    }

    private void slettTestnorgeRelasjonerIntern(List<TestidentDTO> testidenter) {

        var testnorgeIdenter = testidenter.stream()
                .filter(TestidentDTO::isPdl)
                .map(TestidentDTO::getIdent)
                .toList();

        if (!testidenter.isEmpty()) {
            var testnorgeRelasjoner = pdlPersonConsumer.getPdlPersoner(testnorgeIdenter)
                    .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                    .map(PdlPersonBolk::getData)
                    .map(PdlPersonBolk.Data::getHentPersonBolk)
                    .flatMap(Flux::fromIterable)
                    .filter(personBolk -> nonNull(personBolk.getPerson()))
                    .map(person -> Stream.of(
                                    person.getPerson().getSivilstand().stream()
                                            .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                            .toList(),
                                    person.getPerson().getForelderBarnRelasjon().stream()
                                            .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                            .toList(),
                                    person.getPerson().getForeldreansvar().stream()
                                            .map(ForeldreansvarDTO::getAnsvarlig)
                                            .toList(),
                                    person.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                            .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                            .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                            .toList())
                            .flatMap(Collection::stream)
                            .filter(Objects::nonNull)
                            .toList())
                    .flatMap(Flux::fromIterable)
                    .distinct()
                    .collectList()
                    .block();

            if (nonNull(testnorgeRelasjoner)) {
                testnorgeRelasjoner.forEach(bestillingService::slettBestillingByTestIdent);
                testnorgeRelasjoner.forEach(identService::slettTestident);

                testidenter.addAll(testnorgeRelasjoner.stream()
                        .map(ident -> TestidentDTO.builder()
                                .ident(ident)
                                .master(Testident.Master.PDL)
                                .build())
                        .toList());
            }
        }
    }

    private void releaseArtifacts(List<TestidentDTO> identer) {

        clientRegister.forEach(register -> {
            try {
                register.release(identer.stream()
                        .map(TestidentDTO::getIdent)
                        .toList());

            } catch (RuntimeException e) {
                log.error("Feilet å slette fra register, {}", e.getMessage(), e);
            }
        });
    }
}