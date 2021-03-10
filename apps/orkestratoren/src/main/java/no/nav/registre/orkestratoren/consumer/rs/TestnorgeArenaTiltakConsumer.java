package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.consumer.utils.ArenaConsumerUtils;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

import java.util.List;

@Component
public class TestnorgeArenaTiltakConsumer {

    private final ArenaConsumerUtils consumerUtils;

    private UriTemplate arenaOpprettTiltaksdeltakelseUrl;
    private UriTemplate arenaOpprettTiltakspengerUrl;
    private UriTemplate arenaOpprettBarnetilleggUrl;

    public TestnorgeArenaTiltakConsumer(
            @Autowired ArenaConsumerUtils consumerUtils,
            @Value("${testnorge-arena.rest.api.url}") String arenaServerUrl
    ) {
        this.consumerUtils = consumerUtils;
        this.arenaOpprettTiltaksdeltakelseUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tiltaksdeltakelse");
        this.arenaOpprettTiltakspengerUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tiltakspenger");
        this.arenaOpprettBarnetilleggUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/barnetillegg");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettTiltaksdeltakelse(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettTiltaksdeltakelseUrl, syntetiserArenaRequest, "tiltaksdeltakelse");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettTiltakspenger(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettTiltakspengerUrl, syntetiserArenaRequest, "tiltakspenger");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettBarnetillegg(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettBarnetilleggUrl, syntetiserArenaRequest, "barnetillegg");
    }
}
