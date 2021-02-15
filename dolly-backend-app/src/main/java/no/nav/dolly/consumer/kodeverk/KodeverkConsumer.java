package no.nav.dolly.consumer.kodeverk;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK_2;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Component
@RequiredArgsConstructor
public class KodeverkConsumer {

    private static final String KODEVERK_URL_COMPLETE = "/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger?ekskluderUgyldige=true&spraak=nb";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    private static String getNorskBokmaal(Entry<String, java.util.List<KodeverkBetydningerResponse.Betydning>> entry) {

        return entry.getValue().get(0).getBeskrivelser().get("nb").getTekst();
    }

    private static String getKodeverksnavnUrl(String kodeverksnavn) {
        return KODEVERK_URL_COMPLETE.replace("{kodeverksnavn}", kodeverksnavn);
    }

    @Timed(name = "providers", tags = {"operation", "hentKodeverk"})
    public KodeverkBetydningerResponse fetchKodeverkByName(String kodeverk) {

        ResponseEntity<KodeverkBetydningerResponse> kodeverkResponse = getKodeverk(kodeverk);
        return kodeverkResponse.hasBody() ? kodeverkResponse.getBody() : KodeverkBetydningerResponse.builder().build();
    }

    @Cacheable(CACHE_KODEVERK_2)
    @Timed(name = "providers", tags = {"operation", "hentKodeverk"})
    public Map<String, String> getKodeverkByName(String kodeverk) {

        ResponseEntity<KodeverkBetydningerResponse> kodeverkResponse = getKodeverk(kodeverk);
        return kodeverkResponse.hasBody() ? kodeverkResponse.getBody().getBetydninger().entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Entry::getKey, KodeverkConsumer::getNorskBokmaal)) :
                Collections.emptyMap();
    }

    private ResponseEntity<KodeverkBetydningerResponse> getKodeverk(String kodeverk) {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(providersProps.getKodeverk().getUrl() + getKodeverksnavnUrl(kodeverk.replace(" ", "%20"))))
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .build(), KodeverkBetydningerResponse.class);
    }
}
