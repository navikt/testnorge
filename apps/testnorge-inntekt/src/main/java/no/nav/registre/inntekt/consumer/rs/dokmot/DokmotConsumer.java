package no.nav.registre.inntekt.consumer.rs.dokmot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.inntekt.consumer.rs.dokmot.command.OpprettJournalpostCommand;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokmotRequest;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.InntektDokument;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.ProsessertInntektDokument;
import no.nav.registre.inntekt.security.sts.StsOidcService;

@Slf4j
@Component
public class DokmotConsumer {
    private final RestTemplate restTemplate;
    private final StsOidcService oidcService;
    private final UriTemplate url;
    private final ExecutorService executorService;

    public DokmotConsumer(
            @Value("${dokmot.consumer.maxThreads}") Integer maxThreads,
            @Value("${dokmot.joark.rest.api.url}") String joarkUrl,
            RestTemplate restTemplate,
            StsOidcService oidcService
    ) {
        this.url = new UriTemplate(joarkUrl + "/rest/journalpostapi/v1/journalpost");
        this.restTemplate = restTemplate;
        this.oidcService = oidcService;
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }

    private CompletableFuture<ProsessertInntektDokument> opprettJournalpost(String miljoe, InntektDokument inntektDokument, Executor executor, String navCallId) {
        OpprettJournalpostCommand command = new OpprettJournalpostCommand(
                restTemplate,
                oidcService.getIdToken(miljoe),
                url.expand(miljoe),
                new DokmotRequest(inntektDokument),
                navCallId
        );
        return CompletableFuture
                .supplyAsync(command::call, executor)
                .thenApply(response -> new ProsessertInntektDokument(inntektDokument, response));
    }

    @SneakyThrows public List<ProsessertInntektDokument> opprettJournalpost(String miljoe, List<InntektDokument> inntektDokumentList, String navCallId) {
        log.info("Oppretter {} journalpost(er) i miljø {} for inntektsdokument(er). Nav-Call-Id: {}", inntektDokumentList.size(), miljoe, navCallId);

        List<CompletableFuture<ProsessertInntektDokument>> completableDokmotList = new ArrayList<>(inntektDokumentList.size());
        inntektDokumentList.forEach(inntektDokument -> completableDokmotList.add(opprettJournalpost(miljoe, inntektDokument, executorService, navCallId)));

        List<ProsessertInntektDokument> responserFraJoark = new ArrayList<>(inntektDokumentList.size());

        for (var future : completableDokmotList) {
            try {
                responserFraJoark.add(future.get());
            } catch (Exception e) {
                log.error("Uventet feil ved opprettelse av journalpost i joark: ", e);
                throw e;
            }
        }
        log.info("{} journalpost(er) ble opprettet i miljø {}.", responserFraJoark.size(), miljoe);
        return responserFraJoark;
    }
}
