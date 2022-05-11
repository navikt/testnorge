package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeNavEndringsmeldingerConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.RsPureXmlMessageResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestnorgeSkdService {

    private final TestnorgeSkdConsumer testnorgeSkdConsumer;

    private final TestnorgeNavEndringsmeldingerConsumer testnorgeNavEndringsmeldingerConsumer;

    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    public ResponseEntity<SkdMeldingerTilTpsRespons> genererSkdmeldinger(
            Long avspillergruppeId,
            String miljoe,
            Map<String, Integer> antallMeldingerPerEndringskode
    ) {

        var response = testnorgeSkdConsumer.startSyntetisering(new SyntetiserSkdmeldingerRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskode));
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            log.warn("Noe feilet under syntetisering av skd-meldinger. Vennligst se loggene til Testnorge-Skd for mer informasjon");
        } else {
            var skdMeldingerTilTpsRespons = (SkdMeldingerTilTpsRespons) response.getBody();
            if (skdMeldingerTilTpsRespons != null) {
                log.info("{} skd-meldinger sendt til TPS.", skdMeldingerTilTpsRespons.getAntallSendte());
                hodejegerenHistorikkConsumer.oppdaterHodejegerenCache(avspillergruppeId);
            } else {
                log.warn("Fikk ingen response fra testnorge-skd. Ingen meldinger sendt til TPS.");
            }
        }
        return response;
    }

    public List<RsPureXmlMessageResponse> genererNavmeldinger(
            SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest
    ) {
        var response = testnorgeNavEndringsmeldingerConsumer.startSyntetisering(syntetiserNavmeldingerRequest);
        return response.getBody();
    }
}