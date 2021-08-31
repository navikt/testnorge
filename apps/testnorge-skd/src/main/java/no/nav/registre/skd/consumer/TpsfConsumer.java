package no.nav.registre.skd.consumer;

import com.google.common.collect.Lists;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.skd.consumer.command.tpsf.DeleteIdenterFraTpsCommand;
import no.nav.registre.skd.consumer.command.tpsf.GetMeldingerMedIdsCommand;
import no.nav.registre.skd.consumer.command.tpsf.GetMeldingsIdsCommand;
import no.nav.registre.skd.consumer.command.tpsf.PostHentMeldingsIdsCommand;
import no.nav.registre.skd.consumer.command.tpsf.PostOppdaterSkdmeldingCommand;
import no.nav.registre.skd.consumer.command.tpsf.PostSaveSkdEndringsmeldingerTpsfCommand;
import no.nav.registre.skd.consumer.command.tpsf.PostSendSkdMeldingerTpsCommand;
import no.nav.registre.skd.consumer.command.tpsf.PostSlettMeldingerTpsfCommand;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.requests.SlettSkdmeldingerRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Component
@Slf4j
public class TpsfConsumer {

    private final WebClient webClient;

    private static final int PAGE_SIZE = 100;

    public TpsfConsumer(
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.webClient = WebClient.builder().baseUrl(serverUrl)
                .defaultHeaders(header -> header.setBasicAuth(username, password))
                .build();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> saveSkdEndringsmeldingerInTPSF(
            Long gruppeId,
            List<RsMeldingstype> skdmeldinger
    ) {
        if (log.isInfoEnabled()) {
            log.info("Lagrer {} skd endringsmeldinger i tps-forvalteren med gruppe id {}", skdmeldinger.size(), gruppeId);
        }
        return new PostSaveSkdEndringsmeldingerTpsfCommand(gruppeId, skdmeldinger, webClient).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public SkdMeldingerTilTpsRespons sendSkdmeldingerToTps(
            Long gruppeId,
            SendToTpsRequest sendToTpsRequest
    ) {
        log.info("Sender skd-meldinger med avspillergruppe {} til tps", gruppeId);
        return new PostSendSkdMeldingerTpsCommand(gruppeId, sendToTpsRequest, webClient).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIdsFromAvspillergruppe(Long gruppeId) {
        return new GetMeldingsIdsCommand(gruppeId, webClient).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIderTilhoerendeIdenter(
            Long avspillergruppeId,
            List<String> identer
    ) {
        return new PostHentMeldingsIdsCommand(avspillergruppeId, identer, webClient).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public HttpStatus slettMeldingerFraTpsf(List<Long> meldingIder) {
        return new PostSlettMeldingerTpsfCommand(SlettSkdmeldingerRequest.builder().ids(meldingIder).build(), webClient).call();
    }

    @Timed
    public ResponseEntity<Object> slettIdenterFraTps(
            List<String> miljoer,
            List<String> identer
    ) {
        var response = ResponseEntity.ok().build();
        var miljoerSomString = String.join(",", miljoer);

        List<List<String>> identerPartisjonert = Lists.partition(identer, PAGE_SIZE);
        for (var partisjon : identerPartisjonert) {
            var identerSomString = String.join(",", partisjon);
            try {
                new DeleteIdenterFraTpsCommand(miljoerSomString, identerSomString, webClient).call();
            } catch (HttpClientErrorException e) {
                log.error("Kunne ikke slette ident fra TPS. ", e);
            }
        }
        return response;
    }

    public List<RsMeldingstype> getMeldingerMedIds(List<String> ids) {
        List<RsMeldingstype> response = new ArrayList<>();
        for (var partisjonerteIder : Lists.partition(ids, 80)) {
            var identer = String.join(",", partisjonerteIder);
            try {
                var res = new GetMeldingerMedIdsCommand(identer, webClient).call();
                if (res != null) {
                    response.addAll(res);
                }
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke hente meldinger med ider {}", partisjonerteIder, e);
            }
        }
        return response;
    }

    public List<Long> oppdaterSkdMeldinger(List<RsMeldingstype> meldinger) {
        if (!meldinger.isEmpty()) {
            var response = new PostOppdaterSkdmeldingCommand(meldinger, webClient).call();
            if (!response.is2xxSuccessful()) {
                List<Long> meldingIdsSomIkkeKunneOppdateres = meldinger.stream().map(RsMeldingstype::getId).collect(Collectors.toList());
                log.error("Kunne ikke oppdatere meldinger med id-er: {}", meldingIdsSomIkkeKunneOppdateres);
                return meldingIdsSomIkkeKunneOppdateres;
            }
            log.info("Oppdaterte meldinger med id-er: {}", meldinger.stream().map(RsMeldingstype::getId).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
}
