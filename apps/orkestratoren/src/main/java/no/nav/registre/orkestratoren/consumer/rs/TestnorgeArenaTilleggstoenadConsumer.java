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
public class TestnorgeArenaTilleggstoenadConsumer {

    private final ArenaConsumerUtils consumerUtils;

    private UriTemplate arenaOpprettBoutgifterUrl;
    private UriTemplate arenaOpprettDagligReiseUrl;
    private UriTemplate arenaOpprettFlyttingUrl;
    private UriTemplate arenaOpprettLaeremidlerUrl;
    private UriTemplate arenaOpprettHjemreiseUrl;
    private UriTemplate arenaOpprettReiseObligatoriskSamlingUrl;

    private UriTemplate arenaOpprettTilsynBarnUrl;
    private UriTemplate arenaOpprettTilsynFamiliemedlemmerUrl;
    private UriTemplate arenaOpprettTilsynBarnArbeidssoekereUrl;
    private UriTemplate arenaOpprettTilsynFamiliemedlemmerArbeidssoekereUrl;

    private UriTemplate arenaOpprettBoutgifterArbeidssoekereUrl;
    private UriTemplate arenaOpprettDagligReiseArbeidssoekereUrl;
    private UriTemplate arenaOpprettFlyttingArbeidssoekereUrl;
    private UriTemplate arenaOpprettLaeremidlerArbeidssoekereUrl;
    private UriTemplate arenaOpprettHjemreiseArbeidssoekereUrl;
    private UriTemplate arenaOpprettReiseObligatoriskSamlingArbeidssoekereUrl;
    private UriTemplate arenaOpprettReisestoenadArbeidssoekereUrl;

    public TestnorgeArenaTilleggstoenadConsumer(
            @Autowired ArenaConsumerUtils consumerUtils,
            @Value("${testnorge-arena.rest.api.url}") String arenaServerUrl
    ) {
        this.consumerUtils = consumerUtils;

        this.arenaOpprettBoutgifterUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/boutgifter");
        this.arenaOpprettDagligReiseUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/dagligReise");
        this.arenaOpprettFlyttingUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/flytting");
        this.arenaOpprettLaeremidlerUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/laeremidler");
        this.arenaOpprettHjemreiseUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/hjemreise");
        this.arenaOpprettReiseObligatoriskSamlingUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/reiseObligatoriskSamling");

        this.arenaOpprettTilsynBarnUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/tilsynBarn");
        this.arenaOpprettTilsynFamiliemedlemmerUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/tilsynFamiliemedlemmer");
        this.arenaOpprettTilsynBarnArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/tilsynBarnArbeidssoekere");
        this.arenaOpprettTilsynFamiliemedlemmerArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/tilsynFamiliemedlemmerArbeidssoekere");

        this.arenaOpprettBoutgifterArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/boutgifterArbeidssoekere");
        this.arenaOpprettDagligReiseArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/dagligReiseArbeidssoekere");
        this.arenaOpprettFlyttingArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/flyttingArbeidssoekere");
        this.arenaOpprettLaeremidlerArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/laeremidlerArbeidssoekere");
        this.arenaOpprettHjemreiseArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/hjemreiseArbeidssoekere");
        this.arenaOpprettReiseObligatoriskSamlingArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/reiseObligatoriskSamlingArbeidssoekere");
        this.arenaOpprettReisestoenadArbeidssoekereUrl = new UriTemplate(arenaServerUrl + "/v1/syntetisering/generer/tillegg/reisestoenadArbeidssoekere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettBoutgifter(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettBoutgifterUrl, syntetiserArenaRequest, "boutgifter");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettDagligReise(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettDagligReiseUrl, syntetiserArenaRequest, "daglig-reise");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettFlytting(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettFlyttingUrl, syntetiserArenaRequest, "flytting");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettLaeremidler(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettLaeremidlerUrl, syntetiserArenaRequest, "læremidler");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettHjemreise(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettHjemreiseUrl, syntetiserArenaRequest, "hjemreise");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettReiseObligatoriskSamling(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettReiseObligatoriskSamlingUrl, syntetiserArenaRequest, "reise-obligatorisk-samling");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettTilsynBarn(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettTilsynBarnUrl, syntetiserArenaRequest, "tilsyn-barn");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettTilsynFamiliemedlemmer(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettTilsynFamiliemedlemmerUrl, syntetiserArenaRequest, "tilsyn-familiemedlemmer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettTilsynBarnArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettTilsynBarnArbeidssoekereUrl, syntetiserArenaRequest, "tilsyn-barn-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettTilsynFamiliemedlemmerArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettTilsynFamiliemedlemmerArbeidssoekereUrl, syntetiserArenaRequest, "tilsyn-familiemedlemmer-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettBoutgifterArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettBoutgifterArbeidssoekereUrl, syntetiserArenaRequest, "boutgifter-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettDagligReiseArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettDagligReiseArbeidssoekereUrl, syntetiserArenaRequest, "daglig-reise-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettFlyttingArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettFlyttingArbeidssoekereUrl, syntetiserArenaRequest, "flytting-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettLaeremidlerArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettLaeremidlerArbeidssoekereUrl, syntetiserArenaRequest, "læremidler-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettHjemreiseArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettHjemreiseArbeidssoekereUrl, syntetiserArenaRequest, "hjemreise-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettReiseObligatoriskSamlingArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettReiseObligatoriskSamlingArbeidssoekereUrl, syntetiserArenaRequest, "reise-obligatorisk-samling-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public List<NyttVedtakResponse> opprettReisestoenadArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return consumerUtils.sendRequest(arenaOpprettReisestoenadArbeidssoekereUrl, syntetiserArenaRequest, "reisestønad-arbeidssøkere");
    }
}
