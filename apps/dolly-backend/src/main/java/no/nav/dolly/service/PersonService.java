package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.dto.TestidentDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final List<ClientRegister> clientRegister;
    private final PdlDataConsumer pdlDataConsumer;
    private final ResponseBodyResultHandler responseBodyResultHandler;

    @Transactional
    public Mono<Void> recyclePersoner(List<TestidentDTO> testidenter) {

        return pdlDataConsumer.slettPdl(testidenter.stream()
                        .map(TestidentDTO::getIdent)
                        .toList())
                .doOnSuccess(response -> log.info("Slettet {} identer mot PDL-forvalter", testidenter.size()))
                .doOnNext(response -> releaseArtifacts(testidenter))
                .then();
    }

    @Async
    protected void releaseArtifacts(List<TestidentDTO> testidenter) {

        var identer=  testidenter.stream()
                .map(TestidentDTO::getIdent)
                .toList();

        clientRegister.forEach(register -> register.release(identer));
    }
}