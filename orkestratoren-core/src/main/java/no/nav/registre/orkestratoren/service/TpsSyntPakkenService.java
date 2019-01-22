package no.nav.registre.orkestratoren.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;

@Service
@Slf4j
public class TpsSyntPakkenService {

    @Autowired
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    public ResponseEntity produserOgSendSkdmeldingerTilTpsIMiljoer(Long avspillergruppeId,
            String miljoe,
            Map<String, Integer> antallMeldingerPerEndringskode) {

        ResponseEntity response = testnorgeSkdConsumer.startSyntetisering(new GenereringsOrdreRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskode));
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            log.warn("Noe feilet under syntetisering av skd-meldinger. Vennligst se loggene til Testnorge-Skd for mer informasjon");
        } else {
            if (response.getBody() != null) {
                SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = (SkdMeldingerTilTpsRespons) response.getBody();
                log.info("{} skd-meldinger sendt til TPS.", skdMeldingerTilTpsRespons.getAntallSendte());
            }
        }
        return response;
    }
}