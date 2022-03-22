package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.arena.*;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.ArenaForvalterenProxyProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.EndreInnsatsbehovRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.FinnTiltakRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.arena.EndreInnsatsbehovResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class ArenaForvalterConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    public ArenaForvalterConsumer(
            ArenaForvalterenProxyProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.tokenExchange = tokenExchange;
    }

    public NyeBrukereResponse sendBrukereTilArenaForvalter(
            List<NyBruker> nyeBrukere
    ) {
        try {
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new PostArenaBrukerCommand(nyeBrukere, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke å sende inn ny(e) bruker(e) til Arena-forvalteren.", e);
            throw e;
        }
    }

    public Map<String, List<NyttVedtakResponse>> opprettRettighet(List<RettighetRequest> rettigheter) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();
        for (var rettighet : rettigheter) {
            NyttVedtakResponse response = null;
            try {
                response = tokenExchange.exchange(serviceProperties)
                        .flatMap(accessToken -> new PostRettighetCommand(rettighet, accessToken.getTokenValue(), webClient).call())
                        .block();
            } catch (Exception e) {
                log.error("Kunne ikke opprette rettighet i arena-forvalteren.", e);
            }

            if (nonNull(response)) {
                if (responses.containsKey(rettighet.getPersonident())) {
                    responses.get(rettighet.getPersonident()).add(response);
                } else {
                    responses.put(rettighet.getPersonident(), new ArrayList<>(Collections.singletonList(response)));
                }
            }
            if (isNull(response) || (nonNull(response.getFeiledeRettigheter()) && !response.getFeiledeRettigheter().isEmpty())) {
                log.info("Innsendt rettighet feilet. Stopper videre innsending av historikk for ident: "
                        + rettighet.getPersonident());
                break;
            }
        }
        return responses;
    }

    public NyttVedtakResponse finnTiltak(FinnTiltakRequest rettighet) {
        try {
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new PostFinnTiltakCommand(rettighet, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente tiltak for ident {} i miljø {}", rettighet.getPersonident(), rettighet.getMiljoe(), e);
            return null;
        }
    }

    public void endreInnsatsbehovForBruker(EndreInnsatsbehovRequest endreRequest) {
        EndreInnsatsbehovResponse response = null;
        try {
            response = tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new PostEndreInnsatsbehovCommand(endreRequest, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Kunne ikke endre innsatsbehov i arena forvalteren.", e);
        }

        if (isNull(response) || (nonNull(response.getNyeEndreInnsatsbehovFeilList()) &&
                !response.getNyeEndreInnsatsbehovFeilList().isEmpty())) {
            log.info(String.format("Endring av innsatsbehov for ident %s feilet", endreRequest.getPersonident()));
        }
    }

    public List<Arbeidsoeker> hentArbeidsoekere(
            String personident,
            String eier,
            String miljoe
    ) {
        var queryParams = getQueryParams(personident, eier, miljoe, null);
        NyeBrukereResponse response = null;
        try {
            response = tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new GetArenaBrukereCommand(queryParams, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Fant ikke arbeidssoeker i Arena.", e);
        }

        if (nonNull(response)) {
            return gaaGjennomSider(personident, eier, miljoe, response.getAntallSider(), response.getArbeidsoekerList().size());
        } else {
            return new ArrayList<>();
        }
    }

    private MultiValueMap<String, String> getQueryParams(
            String personident,
            String eier,
            String miljoe,
            String page
    ) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (!isNullOrEmpty(eier)) {
            queryParams.add("filter-eier", eier);
        }
        if (!isNullOrEmpty(miljoe)) {
            queryParams.add("filter-miljoe", miljoe);
        }
        if (!isNullOrEmpty(personident)) {
            queryParams.add("filter-personident", personident);
        }
        if (!isNullOrEmpty(page)) {
            queryParams.add("page", page);
        }
        return queryParams;
    }

    private List<Arbeidsoeker> gaaGjennomSider(
            String personident,
            String eier,
            String miljoe,
            int antallSider,
            int initialLength
    ) {
        List<Arbeidsoeker> arbeidssoekere = new ArrayList<>(antallSider * initialLength);

        for (var page = 0; page < antallSider; page++) {
            var queryParams = getQueryParams(personident, eier, miljoe, page + "");
            NyeBrukereResponse response = null;
            try {
                response = tokenExchange.exchange(serviceProperties)
                        .flatMap(accessToken -> new GetArenaBrukereCommand(queryParams, accessToken.getTokenValue(), webClient).call())
                        .block();
            } catch (Exception e) {
                log.error("Fant ikke arbeidssoeker i Arena.", e);
            }

            if (nonNull(response)) {
                arbeidssoekere.addAll(response.getArbeidsoekerList());
            }
        }

        return arbeidssoekere;
    }
}
