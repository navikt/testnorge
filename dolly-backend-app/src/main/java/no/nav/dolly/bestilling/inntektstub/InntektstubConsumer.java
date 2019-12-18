package no.nav.dolly.bestilling.inntektstub;

import static java.lang.String.format;

import java.net.URI;
import java.util.List;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.properties.ProvidersProps;

@Service
@RequiredArgsConstructor
public class InntektstubConsumer {

    private static final String BESTILLING_INNTEKTER_URL = "/api/v2/inntektsinformasjon";
    private static final String DELETE_INNTEKTER_URL = "/api/v2/personer?norske-identer=";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public ResponseEntity deleteInntekter(String ident) {

        return restTemplate.exchange(RequestEntity.delete(
                URI.create(format("%s%s%s", providersProps.getInntektstub().getUrl(), DELETE_INNTEKTER_URL, ident)))
                .build(), Inntektsinformasjon.class);
    }

    public ResponseEntity<Inntektsinformasjon[]> postInntekter(List<Inntektsinformasjon> inntektsinformasjon) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getInntektstub().getUrl() + BESTILLING_INNTEKTER_URL))
                .body(inntektsinformasjon), Inntektsinformasjon[].class);
    }
}
