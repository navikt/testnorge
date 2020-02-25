package no.nav.dolly.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final TpsfService tpsfService;
    private final List<ClientRegister> clientRegister;

    @Async
    public void recyclePersoner(List<String> identer) {

        recycleTpsfPersoner(identer);
        releaseArtifacts(identer);
    }

    private void recycleTpsfPersoner(List<String> identer) {

        if (!identer.isEmpty()) {
            try {
                tpsfService.deletePersones(identer);
            } catch (HttpClientErrorException e) {
                if (!OK.equals(e.getStatusCode()) && !NOT_FOUND.equals(e.getStatusCode())) {
                    log.error("Sletting av identer i TPSF feilet: {}", e.getMessage(), e);
                }
            }
        }
    }

    private void releaseArtifacts(List<String> identer) {

        clientRegister.forEach(register -> {

            try {
                register.release(identer);

            } catch (RuntimeException e) {
                log.error("Feilet å slette fra register, {}", e.getMessage(), e);
            }
        });
    }
}