package no.nav.registre.arena.core.consumer.rs;

import no.nav.registre.arena.core.consumer.rs.command.PostSyntAapRequestCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependenciesOn;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;

@Component
@DependencyOn(value = "nais-synthdata-arena-aap", external = true)
public class AapSyntConsumer {

    private final WebClient webClient;

    private static final String ARENA_AAP_URL = "/v1/arena/aap";
    private static final String ARENA_AAP_115_URL = "/v1/arena/aap/11_5";
    private static final String ARENA_AAP_UNG_UFOER_URL = "/v1/arena/aap/aaungufor";
    private static final String ARENA_AAP_AATFOR_URL = "/v1/arena/aap/aatfor";
    private static final String ARENA_AAP_FRI_MK = "/v1/arena/aap/fri_mk";


    public AapSyntConsumer(
            @Value("${synt-arena.rest-api.url}") String arenaAapServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaAapServerUrl).build();
    }

    public List<NyttVedtakAap> syntetiserRettighetAap(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_URL).call();
    }

    public List<NyttVedtakAap> syntetiserRettighetAap115(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_115_URL).call();
    }

    public List<NyttVedtakAap> syntetiserRettighetUngUfoer(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_UNG_UFOER_URL).call();
    }

    public List<NyttVedtakAap> syntetiserRettighetTvungenForvaltning(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_AATFOR_URL).call();
    }

    public List<NyttVedtakAap> syntetiserRettighetFritakMeldekort(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_FRI_MK).call();
    }

}
