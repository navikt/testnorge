package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.consumer.utils.ArenaConsumerUtils;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

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
    public void opprettBoutgifter(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettBoutgifterUrl, syntetiserArenaRequest, "boutgifter");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettDagligReise(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettDagligReiseUrl, syntetiserArenaRequest, "daglig-reise");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettFlytting(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettFlyttingUrl, syntetiserArenaRequest, "flytting");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettLaeremidler(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettLaeremidlerUrl, syntetiserArenaRequest, "læremidler");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettHjemreise(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettHjemreiseUrl, syntetiserArenaRequest, "hjemreise");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettReiseObligatoriskSamling(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettReiseObligatoriskSamlingUrl, syntetiserArenaRequest, "reise-obligatorisk-samling");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettTilsynBarn(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettTilsynBarnUrl, syntetiserArenaRequest, "tilsyn-barn");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettTilsynFamiliemedlemmer(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettTilsynFamiliemedlemmerUrl, syntetiserArenaRequest, "tilsyn-familiemedlemmer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettTilsynBarnArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettTilsynBarnArbeidssoekereUrl, syntetiserArenaRequest, "tilsyn-barn-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettTilsynFamiliemedlemmerArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettTilsynFamiliemedlemmerArbeidssoekereUrl, syntetiserArenaRequest, "tilsyn-familiemedlemmer-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettBoutgifterArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettBoutgifterArbeidssoekereUrl, syntetiserArenaRequest, "boutgifter-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettDagligReiseArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettDagligReiseArbeidssoekereUrl, syntetiserArenaRequest, "daglig-reise-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettFlyttingArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettFlyttingArbeidssoekereUrl, syntetiserArenaRequest, "flytting-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettLaeremidlerArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettLaeremidlerArbeidssoekereUrl, syntetiserArenaRequest, "læremidler-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettHjemreiseArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettHjemreiseArbeidssoekereUrl, syntetiserArenaRequest, "hjemreise-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettReiseObligatoriskSamlingArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettReiseObligatoriskSamlingArbeidssoekereUrl, syntetiserArenaRequest, "reise-obligatorisk-samling-arbeidssøkere");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena" })
    public void opprettReisestoenadArbeidssoekere(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        consumerUtils.sendRequest(arenaOpprettReisestoenadArbeidssoekereUrl, syntetiserArenaRequest, "reisestønad-arbeidssøkere");
    }
}
