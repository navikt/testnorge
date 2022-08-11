package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.RsFullmakt;
import no.nav.dolly.domain.resultset.tpsf.RsSimplePerson;
import no.nav.dolly.domain.resultset.tpsf.RsVergemaal;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
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

    private final TpsfService tpsfService;
    private final List<ClientRegister> clientRegister;
    private final PdlDataConsumer pdlDataConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final BestillingService bestillingService;
    private final IdentService identService;

    @Async
    @Transactional
    public void recyclePersoner(List<TestidentDTO> testidenter) {

        var tpsfPersoner = tpsfService.hentTestpersoner(testidenter.stream()
                .filter(TestidentDTO::isTpsf)
                .map(TestidentDTO::getIdent)
                .toList());

        if (testidenter.stream().anyMatch(TestidentDTO::isTpsf)) {

            var identerInkludertRelasjoner = Stream.of(testidenter.stream()
                                    .filter(TestidentDTO::isTpsf)
                                    .map(TestidentDTO::getIdent)
                                    .toList(),
                            tpsfPersoner.stream()
                                    .map(Person::getRelasjoner)
                                    .flatMap(Collection::stream)
                                    .map(Relasjon::getPersonRelasjonMed)
                                    .map(Person::getIdent)
                                    .toList(),
                            tpsfPersoner.stream()
                                    .map(Person::getVergemaal)
                                    .flatMap(Collection::stream)
                                    .map(RsVergemaal::getVerge)
                                    .map(RsSimplePerson::getIdent)
                                    .toList(),
                            tpsfPersoner.stream()
                                    .map(Person::getFullmakt)
                                    .flatMap(Collection::stream)
                                    .map(RsFullmakt::getFullmektig)
                                    .map(RsSimplePerson::getIdent)
                                    .toList(),
                            tpsfPersoner.stream()
                                    .map(Person::getIdentHistorikk)
                                    .flatMap(Collection::stream)
                                    .map(IdentHistorikk::getAliasPerson)
                                    .map(Person::getIdent)
                                    .toList())
                    .flatMap(Collection::stream)
                    .distinct()
                    .toList();

            pdlDataConsumer.slettPdlUtenom(identerInkludertRelasjoner)
                    .subscribe(response -> log.info("Slettet antall {} identer (master TPS) mot PDL-forvalter", identerInkludertRelasjoner.size()));
        }

        if (testidenter.stream().anyMatch(TestidentDTO::isPdlf)) {

            var pdlfIdenter = testidenter.stream()
                    .filter(TestidentDTO::isPdlf)
                    .map(TestidentDTO::getIdent)
                    .toList();

            var pdlfRelasjoner = Flux.range(0, (pdlfIdenter.size() / PAGE_SIZE) + 1)
                    .flatMap(index -> pdlDataConsumer.getPersoner(pdlfIdenter, index * PAGE_SIZE,
                            Math.min((index + 1) * PAGE_SIZE, pdlfIdenter.size())))
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