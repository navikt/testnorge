package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.RsFullmakt;
import no.nav.dolly.domain.resultset.tpsf.RsSimplePerson;
import no.nav.dolly.domain.resultset.tpsf.RsVergemaal;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
import no.nav.dolly.repository.IdentRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final TpsfService tpsfService;
    private final List<ClientRegister> clientRegister;
    private final IdentRepository identRepository;
    private final PdlDataConsumer pdlDataConsumer;

    @Async
    public void recyclePersoner(List<TestidentDTO> testidenter) {

        var tpsfPersoner = tpsfService.hentTestpersoner(testidenter.stream()
                .filter(TestidentDTO::isTpsf)
                .map(TestidentDTO::getIdent)
                .toList());

        if (!tpsfPersoner.isEmpty()) {
            var identerInkludertRelasjoner = Stream.of(tpsfPersoner.stream()
                                    .map(Person::getIdent)
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


            pdlDataConsumer.slettPdlUtenom(identerInkludertRelasjoner);
        }

        if (testidenter.stream().anyMatch(TestidentDTO::isPdlf)) {

            pdlDataConsumer.slettPdl(testidenter.stream()
                    .filter(TestidentDTO::isPdlf)
                    .map(TestidentDTO::getIdent)
                    .toList());
        }

        releaseArtifacts(testidenter);
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