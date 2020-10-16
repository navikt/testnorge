package no.nav.registre.testnorge.personexportapi.consumer.kodeverk;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KodeverkConsumer {

    private static final String KODEVERK_URL_COMPLETE = "/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger?ekskluderUgyldige=true&spraak=nb";

    private final WebClient webClient;

    public KodeverkConsumer(@Value("${consumers.kodeverk.url}") String kodeverkUrl) {
        this.webClient = WebClient.builder().baseUrl(kodeverkUrl).build();
    }

    //    @Cacheable(CACHE_KODEVERK)
    public Map<String, String> getKodeverkByName(String kodeverk) {

        ResponseEntity<KodeverkBetydningerResponse> kodeverkResponse = webClient
                        .get()
                .uri(getKodeverksnavnUrl(kodeverk))
                .header("Nav-Consumer-Id", "Testnorge")
                .header("Nav-Call-Id", UUID.randomUUID().toString())
                .retrieve()
                .toEntity(KodeverkBetydningerResponse.class)
                .block();

        return kodeverkResponse.hasBody() ? kodeverkResponse.getBody().getBetydninger().entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get(0).getBeskrivelser().get("nb").getTekst())) :
                Collections.emptyMap();
    }

    private String getKodeverksnavnUrl(String kodeverksnavn) {
        return KODEVERK_URL_COMPLETE.replace("{kodeverksnavn}", kodeverksnavn);
    }
}
