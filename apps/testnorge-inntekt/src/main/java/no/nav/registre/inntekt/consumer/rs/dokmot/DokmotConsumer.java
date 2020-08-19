package no.nav.registre.inntekt.consumer.rs.dokmot;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokmotResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

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

    public DokmotConsumer(
            @Value("${dokmot.joark.rest.api.url}") String joarkUrl,
            RestTemplate restTemplate,
            StsOidcService oidcService
    ) {
        this.url = new UriTemplate(joarkUrl + "/rest/journalpostapi/v1/journalpost");
        this.restTemplate = restTemplate;
        this.oidcService = oidcService;
    }

    public List<ProsessertInntektDokument> opprettJournalpost(String miljoe, List<InntektDokument> inntektDokumenter, String navCallId) {
        log.info("Oppretter {} journalpost(er) i miljø {} for inntektsdokument(er). Nav-Call-Id: {}", inntektDokumenter.size(), miljoe, navCallId);
        var resultater = new ArrayList<ProsessertInntektDokument>(inntektDokumenter.size());

        for (var inntektDokument : inntektDokumenter) {
            log.info("Sender dokument til joark med eksternReferanseId: {}", inntektDokument.getMetadata().getEksternReferanseId());
            var command = new OpprettJournalpostCommand(restTemplate, oidcService.getIdToken(miljoe), url.expand(miljoe), new DokmotRequest(inntektDokument), navCallId);
            DokmotResponse respons = command.call();
            log.info("Lagt inn dokument i joark med journalpostId: {} og eksternReferanseId: {}", respons.getJournalpostId(), inntektDokument.getMetadata().getEksternReferanseId());
            resultater.add(new ProsessertInntektDokument(inntektDokument, respons));
        }

        log.info("{} journalpost(er) ble opprettet i miljø {}.", resultater.size(), miljoe);
        return resultater;
    }
}
