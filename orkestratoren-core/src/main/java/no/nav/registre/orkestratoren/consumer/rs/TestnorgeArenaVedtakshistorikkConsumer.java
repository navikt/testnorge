package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.consumer.utils.ConsumerUtils;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@Component
public class TestnorgeArenaVedtakshistorikkConsumer {

    private final ConsumerUtils consumerUtils;

    private UriTemplate arenaOpprettVedtakshistorikkUrl;

    public TestnorgeArenaVedtakshistorikkConsumer(
            @Autowired ConsumerUtils consumerUtils,
            @Value("${testnorge-arena.rest.api.url}") String arenaServerUrl
    ) {
        this.consumerUtils = consumerUtils;

        this.arenaOpprettVedtakshistorikkUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/vedtakshistorikk");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettVedtakshistorikk(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettVedtakshistorikkUrl, syntetiserArenaRequest, "vedtakshistorikk");
    }
}
