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
public class TestnorgeArenaAapConsumer {

    private final ArenaConsumerUtils consumerUtils;

    private UriTemplate arenaOpprettAapUrl;
    private UriTemplate arenaOpprettAapUngUfoerUrl;
    private UriTemplate arenaOpprettAapTvungenForvaltningUrl;
    private UriTemplate arenaOpprettAapFritakMeldekortUrl;

    public TestnorgeArenaAapConsumer(
            @Autowired ArenaConsumerUtils consumerUtils,
            @Value("${testnorge-arena.rest.api.url}") String arenaServerUrl
    ) {
        this.consumerUtils = consumerUtils;
        this.arenaOpprettAapUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/rettighet/aap");
        this.arenaOpprettAapUngUfoerUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/rettighet/ungUfoer");
        this.arenaOpprettAapTvungenForvaltningUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/rettighet/tvungenForvaltning");
        this.arenaOpprettAapFritakMeldekortUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/rettighet/fritakMeldekort");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettRettighetAap(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettAapUrl, syntetiserArenaRequest, "aap");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettRettighetAapUngUfoer(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettAapUngUfoerUrl, syntetiserArenaRequest, "ung-ufør");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettRettighetAapTvungenForvaltning(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettAapTvungenForvaltningUrl, syntetiserArenaRequest, "tvungen forvaltning");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettRettighetAapFritakMeldekort(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettAapFritakMeldekortUrl, syntetiserArenaRequest, "fritak-meldekort");
    }
}
