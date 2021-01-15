package no.nav.registre.arena.core.consumer.rs;

import no.nav.registre.arena.core.consumer.rs.command.PostSyntTilleggRequestCommand;
import no.nav.registre.arena.core.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Component
@DependencyOn(value = "nais-synthdata-arena-tilleggsstonad", external = true)
public class TilleggSyntConsumer {

    private final WebClient webClient;

    private static final String BOUTGIFT_URL = "/v1/arena/tilleggsstonad/boutgift";
    private static final String BOUTGIFTER_ARBEIDSSOEKERE_URL = "/v1/arena/tilleggsstonad/boutgifter_arbeidssokere";
    private static final String DAGLIG_REISE_URL = "/v1/arena/tilleggsstonad/daglig_reise";
    private static final String DAGLIG_REISE_ARBEIDSSOEKER_URL = "/v1/arena/tilleggsstonad/daglig_reise_arbeidssoker";
    private static final String FLYTTING_URL = "/v1/arena/tilleggsstonad/flytting";
    private static final String FLYTTING_ARBEIDSSOEKERE_URL = "/v1/arena/tilleggsstonad/flytting_arbeidssokere";
    private static final String LAEREMIDLER_URL = "/v1/arena/tilleggsstonad/laeremidler";
    private static final String LAEREMIDLER_ARBEIDSSOEKERE_URL = "/v1/arena/tilleggsstonad/laeremidler_arbeidssokere";
    private static final String HJEMREISE_URL = "/v1/arena/tilleggsstonad/reise_aktivitet_og_hjemreiser";
    private static final String HJEMREISE_ARBEIDSSOEKERE_URL = "/v1/arena/tilleggsstonad/reise_aktivitet_og_hjemreiser_arbeidssokere";
    private static final String OBLIGATORISK_SAMLING_URL = "/v1/arena/tilleggsstonad/reise_til_obligatorisk_samling";
    private static final String OBLIGATORISK_SAMLING_ARBEIDSSOEKERE_URL = "/v1/arena/tilleggsstonad/reise_til_obligatorisk_samling_arbeidssokere";
    private static final String REISESTONAD_URL = "/v1/arena/tilleggsstonad/reisestonad_til_arbeidssokere";
    private static final String TILSYN_BARN_URL = "/v1/arena/tilleggsstonad/tilsyn_barn";
    private static final String TILSYN_BARN_ARBEIDSSOEKER_URL = "/v1/arena/tilleggsstonad/tilsyn_barn_arbeidssoker";
    private static final String TILSYN_FAMILIEMEDLEMMER_URL = "/v1/arena/tilleggsstonad/tilsyn_familiemedlemmer";
    private static final String TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOEKERE_URL = "/v1/arena/tilleggsstonad/tilsyn_familiemedlemmer_arbeidssokere";

    public TilleggSyntConsumer(
            @Value("${synt-arena-tillegg.rest-api.url}") String arenaTilleggServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(arenaTilleggServerUrl).build();
    }

    public List<NyttVedtakTillegg> opprettBoutgifter(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, BOUTGIFT_URL).call();
    }

    public List<NyttVedtakTillegg> opprettDagligReise(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, DAGLIG_REISE_URL).call();
    }

    public List<NyttVedtakTillegg> opprettFlytting(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, FLYTTING_URL).call();
    }

    public List<NyttVedtakTillegg> opprettLaeremidler(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, LAEREMIDLER_URL).call();
    }

    public List<NyttVedtakTillegg> opprettHjemreise(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, HJEMREISE_URL).call();
    }

    public List<NyttVedtakTillegg> opprettReiseObligatoriskSamling(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, OBLIGATORISK_SAMLING_URL).call();
    }

    public List<NyttVedtakTillegg> opprettTilsynBarn(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, TILSYN_BARN_URL).call();
    }

    public List<NyttVedtakTillegg> opprettTilsynFamiliemedlemmer(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, TILSYN_FAMILIEMEDLEMMER_URL).call();
    }

    public List<NyttVedtakTillegg> opprettTilsynBarnArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, TILSYN_BARN_ARBEIDSSOEKER_URL).call();
    }

    public List<NyttVedtakTillegg> opprettTilsynFamiliemedlemmerArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOEKERE_URL).call();
    }

    public List<NyttVedtakTillegg> opprettBoutgifterArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, BOUTGIFTER_ARBEIDSSOEKERE_URL).call();
    }

    public List<NyttVedtakTillegg> opprettDagligReiseArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, DAGLIG_REISE_ARBEIDSSOEKER_URL).call();
    }

    public List<NyttVedtakTillegg> opprettFlyttingArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, FLYTTING_ARBEIDSSOEKERE_URL).call();
    }

    public List<NyttVedtakTillegg> opprettLaeremidlerArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, LAEREMIDLER_ARBEIDSSOEKERE_URL).call();
    }

    public List<NyttVedtakTillegg> opprettHjemreiseArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, HJEMREISE_ARBEIDSSOEKERE_URL).call();
    }

    public List<NyttVedtakTillegg> opprettReiseObligatoriskSamlingArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, OBLIGATORISK_SAMLING_ARBEIDSSOEKERE_URL).call();
    }

    public List<NyttVedtakTillegg> opprettReisestoenadArbeidssoekere(List<RettighetSyntRequest> syntRequest) {
        return new PostSyntTilleggRequestCommand(webClient, syntRequest, REISESTONAD_URL).call();
    }
}
