package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.repository.IdentRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final TpsfService tpsfService;
    private final List<ClientRegister> clientRegister;
    private final IdentRepository identRepository;
    private final PdlDataConsumer pdlDataConsumer;

    @Async
    public void recyclePersoner(List<String> identer) {

        var testidenter = identRepository.findByIdentIn(identer);

        testidenter.stream()
                .filter(Testident::isTpsf)
                .map(Testident::getIdent)
                .forEach(this::recycleTpsfPerson);

        pdlDataConsumer.slettPdl(testidenter.stream()
                .filter(Testident::isPdlf)
                .map(Testident::getIdent)
                .toList());

        releaseArtifacts(identer);
    }

    private void recycleTpsfPerson(String ident) {

        try {
            tpsfService.deletePerson(ident);

        } catch (WebClientResponseException e) {
            if (!OK.equals(e.getStatusCode()) && !NOT_FOUND.equals(e.getStatusCode())) {
                log.error("Sletting av identer i TPSF feilet: {}", e.getMessage(), e);
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