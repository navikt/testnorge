package no.nav.registre.inntekt.consumer.rs.dokmot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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

    private CompletableFuture<ProsessertInntektDokument> opprettJournalpost(String miljoe, InntektDokument inntektDokument, Executor executor) {
        OpprettJournalpostCommand command = new OpprettJournalpostCommand(
                restTemplate,
                oidcService.getIdToken(miljoe),
                url.expand(miljoe),
                new DokmotRequest(inntektDokument)
        );
        return CompletableFuture
                .supplyAsync(command::call, executor)
                .thenApply(response -> new ProsessertInntektDokument(inntektDokument, response));
    }
    public List<ProsessertInntektDokument> opprettJournalpost(String miljoe, List<InntektDokument> inntektDokumentList) {
        return opprettJournalpost(miljoe, inntektDokumentList, false);
    }

    public List<ProsessertInntektDokument> opprettJournalpost(String miljoe, List<InntektDokument> inntektDokumentList, boolean continueOnError) {
        log.info("Oppretter {} journalpost i miljo {} for inntekt dokument...", inntektDokumentList.size(), miljoe);

        List<CompletableFuture<ProsessertInntektDokument>> completableDokmotList = new ArrayList<>();
        inntektDokumentList.forEach(inntektDokument -> completableDokmotList.add(opprettJournalpost(miljoe, inntektDokument, executorService)));

        List<ProsessertInntektDokument> responses = completableDokmotList.stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                log.error("Klarer ikke å opprette jornalpost.", e);
                if(continueOnError){
                    return null;
                }
                throw new RuntimeException("Feil ved opprettelse av journalpost for inntektsmelding", e);
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        log.info("{} journalpost opprettet i miljø {}.", responses.size(), miljoe);
        if (responses.size() < inntektDokumentList.size()) {
            log.error("Bare {}/{} journalposter opprettet", responses.size(), inntektDokumentList.size());
        }
        return responses;
    }
}
