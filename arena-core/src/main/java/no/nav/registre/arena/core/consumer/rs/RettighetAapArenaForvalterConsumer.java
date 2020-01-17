package no.nav.registre.arena.core.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@Slf4j
@Component
public class RettighetAapArenaForvalterConsumer {

    private static final String NAV_CALL_ID = "ORKESTRATOREN";
    private static final String NAV_CONSUMER_ID = "ORKESTRATOREN";

    private final RestTemplate restTemplate;

    private UriTemplate opprettAapRettighetUrl;
    private UriTemplate opprettAap115RettighetUrl;
    private UriTemplate opprettUngUfoerRettighetUrl;
    private UriTemplate opprettTvungenForvaltningRettighetUrl;
    private UriTemplate opprettFritakMeldekortRettighetUrl;

    public RettighetAapArenaForvalterConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.opprettAapRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aap");
        this.opprettAap115RettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aap115");
        this.opprettUngUfoerRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapungufor");
        this.opprettTvungenForvaltningRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aaptvungenforvaltning");
        this.opprettFritakMeldekortRettighetUrl = new UriTemplate(arenaForvalterServerUrl + "/v1/aapfritakmeldekort");
    }

    public List<NyttVedtakResponse> opprettRettighet(List<RettighetRequest> rettigheter) {
        //        ObjectMapper objectMapper = new ObjectMapper();
        List<NyttVedtakResponse> responses = new ArrayList<>(rettigheter.size());
        for (var rettighet : rettigheter) {

            UriTemplate url;
            //            StringBuilder stringBuilder = new StringBuilder("\n");
            if (rettighet instanceof RettighetAapRequest) {
                url = opprettAapRettighetUrl;
                //                System.out.println("----------------------");
                //                for (NyttVedtakAap nyttVedtakAap : rettighet.getVedtakAap()) {
                //                    stringBuilder.append("vedtakstype - ").append(nyttVedtakAap.getVedtaktype()).append("   ");
                //                    stringBuilder.append("aktivitetsfase - ").append(nyttVedtakAap.getAktivitetsfase()).append("   ");
                //                    stringBuilder.append("datoMottatt - ").append(nyttVedtakAap.getDatoMottatt()).append("   ");
                //                    stringBuilder.append("fraDato - ").append(nyttVedtakAap.getFraDato()).append("   ");
                //                    if (nyttVedtakAap.getJustertFra() != null && !nyttVedtakAap.getJustertFra().isEmpty()) {
                //                        stringBuilder.append("justertFra - ").append(nyttVedtakAap.getJustertFra()).append("   ");
                //                    }
                //                    stringBuilder.append("tilDato - ").append(nyttVedtakAap.getTilDato()).append("\n");
                //                }
            } else if (rettighet instanceof RettighetAap115Request) {
                url = opprettAap115RettighetUrl;
            } else if (rettighet instanceof RettighetUngUfoerRequest) {
                url = opprettUngUfoerRettighetUrl;
            } else if (rettighet instanceof RettighetTvungenForvaltningRequest) {
                url = opprettTvungenForvaltningRettighetUrl;
            } else if (rettighet instanceof RettighetFritakMeldekortRequest) {
                url = opprettFritakMeldekortRettighetUrl;
            } else {
                throw new RuntimeException("Unkown URL");
            }

            //            try {
            //                log.info("Legger til syntetisk rettighet: {}", objectMapper.writeValueAsString(rettighet));
            //            } catch (JsonProcessingException e) {
            //                log.error("Kunne ikke printe rettighet", e);
            //            }

            //            log.info(stringBuilder.toString());

            //            List<NyttVedtakAap> vedtakAap = rettighet.getVedtakAap();
            //            if (rettighet instanceof RettighetAapRequest) {
            //                for (var vedtak : vedtakAap) {
            //
            //                    RettighetRequest rettighetRequest = new RettighetAapRequest(new ArrayList<>(Collections.singletonList(vedtak)));
            //                    rettighetRequest.setMiljoe(rettighet.getMiljoe());
            //                    rettighetRequest.setPersonident(rettighet.getPersonident());
            //                    for (var vilkaar : vedtak.getVilkaar()) {// test om dette hjelper
            //                        if (vilkaar.getStatus().isEmpty()) {
            //                            vilkaar.setStatus("V");
            //                        }
            //                    }
            //                    var postRequest = RequestEntity.post(url.expand())
            //                            .header("Nav-Call-Id", NAV_CALL_ID)
            //                            .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
            //                            .body(rettighetRequest);
            //                    NyttVedtakResponse response = restTemplate.exchange(postRequest, NyttVedtakResponse.class).getBody();
            //                    if (!response.getFeiledeRettigheter().isEmpty()) {
            //                        try {
            //                            log.info("\nVedtak feilet. \n\tFeilmeling: {} - \n\tVedtak: {}",
            //                                    objectMapper.writeValueAsString(response.getFeiledeRettigheter().get(0)),
            //                                    objectMapper.writeValueAsString(vedtak));
            //                        } catch (JsonProcessingException e) {
            //                            e.printStackTrace();
            //                        }
            //                    }
            //                    responses.add(response);
            //                }
            //                System.out.println("----------------------");
            //            } else {
            var postRequest = RequestEntity.post(url.expand())
                    .header("Nav-Call-Id", NAV_CALL_ID)
                    .header("Nav-Consumer-Id", NAV_CONSUMER_ID)
                    .body(rettighet);
            responses.add(restTemplate.exchange(postRequest, NyttVedtakResponse.class).getBody());
            //            }

        }
        return responses;
    }
}
