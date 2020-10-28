package no.nav.registre.skd.consumer;

import com.google.common.collect.Lists;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.commands.tpsf.HentMeldingIdFraAvspillergruppeCommand;
import no.nav.registre.skd.commands.tpsf.HentMeldingIdFraAvspillergruppeMedTilhoerendeIdenterCommand;
import no.nav.registre.skd.commands.tpsf.HentMeldingerCommand;
import no.nav.registre.skd.commands.tpsf.LagreSkdEndringseldingerITpsfCommand;
import no.nav.registre.skd.commands.tpsf.OppdaterSkdMeldingerCommand;
import no.nav.registre.skd.commands.tpsf.SendEndringsmeldingTilTpsCommand;
import no.nav.registre.skd.commands.tpsf.SlettMeldingerFraTpsfCommand;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.requests.SlettSkdmeldingerRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@DependencyOn(value = "tps-forvalteren", external = true)
public class TpsfConsumer {

    private final WebClient webClient;

    public TpsfConsumer(
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> saveSkdEndringsmeldingerInTPSF(
            Long gruppeId,
            List<RsMeldingstype> skdmeldinger
    ) {
        log.info("Lagrer {} skd endringsmeldinger i tps-forvalteren med gruppe id {}", skdmeldinger.size(), gruppeId);
        return new LagreSkdEndringseldingerITpsfCommand(webClient, gruppeId, skdmeldinger).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public SkdMeldingerTilTpsRespons sendSkdmeldingerToTps(
            Long gruppeId,
            SendToTpsRequest sendToTpsRequest
    ) {
        return new SendEndringsmeldingTilTpsCommand(webClient, gruppeId, sendToTpsRequest).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIdsFromAvspillergruppe(Long gruppeId) {
        return new HentMeldingIdFraAvspillergruppeCommand(webClient, gruppeId).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIderTilhoerendeIdenter(
            Long avspillergruppeId,
            List<String> identer
    ) {
        return new HentMeldingIdFraAvspillergruppeMedTilhoerendeIdenterCommand(webClient, avspillergruppeId, identer).call();
    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "tpsf" })
    public Boolean slettMeldingerFraTpsf(List<Long> meldingIder) {
        return new SlettMeldingerFraTpsfCommand(webClient, meldingIder).call();
    /*    var postRequest = RequestEntity.post(uriSlettMeldinger.expand()).body(SlettSkdmeldingerRequest.builder().ids(meldingIder).build());
        return restTemplate.exchange(postRequest, String.class);*/
    }

    @Timed
    public ResponseEntity<Object> slettIdenterFraTps(
            List<String> miljoer,
            List<String> identer
    ) {
//        var response = ResponseEntity.ok().build();
//        var miljoerSomString = String.join(",", miljoer);
//
//        List<List<String>> identerPartisjonert = Lists.partition(identer, PAGE_SIZE);
//        for (var partisjon : identerPartisjonert) {
//            var identerSomString = String.join(",", partisjon);
//            var deleteRequest = RequestEntity.delete(uriSlettIdenterFraTps.expand(miljoerSomString, identerSomString)).build();
//            try {
//                restTemplate.exchange(deleteRequest, ResponseEntity.class);
//            } catch (HttpClientErrorException e) {
//                log.error("Kunne ikke slette ident fra TPS. ", e);
//            }
//        }
//        return response;
        return null;
    }

    public List<RsMeldingstype> getMeldingerMedIds(List<String> ids) {
        return new HentMeldingerCommand(webClient, ids).call();
    }

    public List<Long> oppdaterSkdMeldinger(List<RsMeldingstype> meldinger) {
        return new OppdaterSkdMeldingerCommand(webClient, meldinger).call();
    }
}
