package no.nav.dolly.bestilling.inntektstub;

import static java.lang.String.format;

import java.net.URI;
import java.util.List;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektskomponenten.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektskomponenten.domain.ValiderInntekt;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Service
@RequiredArgsConstructor
public class InntektstubConsumer {

    private static final String INNTEKTER_URL = "/api/v2/inntektsinformasjon";
    private static final String DELETE_INNTEKTER_URL = "/api/v2/personer?norske-identer=";
    private static final String VALIDER_INNTEKTER_URL = "/api/v2/valider";
    private static final String GET_INNTEKTER_URL = INNTEKTER_URL + "?norske-identer=";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "consumer", tags = { "operation", "inntk_getInntekter" })
    public ResponseEntity getInntekter(String ident) {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(format("%s%s%s", providersProps.getInntektstub().getUrl(), GET_INNTEKTER_URL, ident)))
                .build(), JsonNode.class);
    }

    @Timed(name = "consumer", tags = { "operation", "inntk_deleteInntekter" })
    public ResponseEntity deleteInntekter(String ident) {

        return restTemplate.exchange(RequestEntity.delete(
                URI.create(format("%s%s%s", providersProps.getInntektstub().getUrl(), DELETE_INNTEKTER_URL, ident)))
                .build(), Inntektsinformasjon.class);
    }

    @Timed(name = "consumer", tags = { "operation", "inntk_postInntekter" })
    public ResponseEntity<Inntektsinformasjon[]> postInntekter(List<Inntektsinformasjon> inntektsinformasjon) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getInntektstub().getUrl() + INNTEKTER_URL))
                .body(inntektsinformasjon), Inntektsinformasjon[].class);
    }

    @Timed(name = "consumer", tags = { "operation", "inntk_validerInntekt" })
    public ResponseEntity validerInntekter(ValiderInntekt validerInntekt) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getInntektstub().getUrl() + VALIDER_INNTEKTER_URL))
                .body(validerInntekt), Object.class);
    }
}
