package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.dto.TestidentDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final List<ClientRegister> clientRegister;
    private final PdlDataConsumer pdlDataConsumer;

    @Async
    @Transactional
    public void recyclePersoner(List<TestidentDTO> testidenter) {

        pdlDataConsumer.slettPdl(testidenter.stream()
                        .map(TestidentDTO::getIdent)
                        .toList())
                .doOnSuccess(response -> log.info("Slettet {} identer mot PDL-forvalter", testidenter.size()))
                .then(releaseArtifacts(testidenter))
    }

    private void releaseArtifacts(List<TestidentDTO> testidenter) {

        return Flux.fromIterable(testidenter)
                .map(TestidentDTO::getIdent)
                .collectList()
                .flatMap(identer -> Flux.fromIterable(clientRegister)
                        .flatMap(client -> client.release(identer))
                        .then());
    }
}