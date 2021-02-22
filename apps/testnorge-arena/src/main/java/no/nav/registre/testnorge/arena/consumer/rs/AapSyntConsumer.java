package no.nav.registre.testnorge.arena.consumer.rs;

import no.nav.registre.testnorge.arena.consumer.rs.command.synt.PostSyntAapRequestCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.testnorge.arena.consumer.rs.request.synt.SyntRequest;
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

    public List<NyttVedtakAap> syntetiserRettighetAap(List<SyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_URL).call();
    }

    public List<NyttVedtakAap> syntetiserRettighetAap115(List<SyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_115_URL).call();
    }

    public List<NyttVedtakAap> syntetiserRettighetUngUfoer(List<SyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_UNG_UFOER_URL).call();
    }

    public List<NyttVedtakAap> syntetiserRettighetTvungenForvaltning(List<SyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_AATFOR_URL).call();
    }

    public List<NyttVedtakAap> syntetiserRettighetFritakMeldekort(List<SyntRequest> syntRequest) {
        return new PostSyntAapRequestCommand(webClient, syntRequest, ARENA_AAP_FRI_MK).call();
    }

}
