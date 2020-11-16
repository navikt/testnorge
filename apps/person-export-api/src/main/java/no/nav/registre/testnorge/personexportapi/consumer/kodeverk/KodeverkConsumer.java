package no.nav.registre.testnorge.personexportapi.consumer.kodeverk;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@EnableCaching
@CacheConfig(cacheNames = "Kodeverk")
public class KodeverkConsumer {

    private static final String KODEVERK_URL_COMPLETE = "/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger?ekskluderUgyldige=true&spraak=nb";

    private final WebClient webClient;

    public KodeverkConsumer(@Value("${consumers.kodeverk.url}") String kodeverkUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(kodeverkUrl)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();
    }

    public String getKodeverkOppslag(String kodeverk, String verdi) {
        try {
            return isNotBlank(verdi) ? getKodeverkByName(kodeverk).get(verdi).stream().findFirst()
                    .map(betydning -> betydning.getBeskrivelser().get("nb").getTekst())
                    .orElse(null) : null;

        } catch (RuntimeException e) {
            return null;
        }
    }

    @Cacheable(sync = true)
    public Map<String, List<KodeverkBetydningerResponse.Betydning>> getKodeverkByName(String kodeverk) {

        ResponseEntity<KodeverkBetydningerResponse> kodeverkResponse = webClient
                .get()
                .uri(getKodeverksnavnUrl(kodeverk))
                .header("Nav-Consumer-Id", "Testnorge")
                .header("Nav-Call-Id", UUID.randomUUID().toString())
                .retrieve()
                .toEntity(KodeverkBetydningerResponse.class)
                .block();

        return kodeverkResponse.hasBody() ? kodeverkResponse.getBody().getBetydninger() : Collections.emptyMap();
    }

    private String getKodeverksnavnUrl(String kodeverksnavn) {
        return KODEVERK_URL_COMPLETE.replace("{kodeverksnavn}", kodeverksnavn);
    }
}
