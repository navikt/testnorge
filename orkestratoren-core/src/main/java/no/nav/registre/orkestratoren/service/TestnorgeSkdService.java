package no.nav.registre.orkestratoren.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeNavEndringsmeldingerConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.RsPureXmlMessageResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;

@Service
@Slf4j
public class TestnorgeSkdService {

    @Autowired
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Autowired
    private TestnorgeNavEndringsmeldingerConsumer testnorgeNavEndringsmeldingerConsumer;

    public ResponseEntity genererSkdmeldinger(Long avspillergruppeId,
            String miljoe,
            Map<String, Integer> antallMeldingerPerEndringskode) {

        ResponseEntity response = testnorgeSkdConsumer.startSyntetisering(new SyntetiserSkdmeldingerRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskode));
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            log.warn("Noe feilet under syntetisering av skd-meldinger. Vennligst se loggene til Testnorge-Skd for mer informasjon");
        } else {
            SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = (SkdMeldingerTilTpsRespons) response.getBody();
            if (skdMeldingerTilTpsRespons != null) {
                log.info("{} skd-meldinger sendt til TPS.", skdMeldingerTilTpsRespons.getAntallSendte());
            }
        }
        return response;
    }

    public List<RsPureXmlMessageResponse> genererNavmeldinger(SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest) {
        ResponseEntity<List<RsPureXmlMessageResponse>> response = testnorgeNavEndringsmeldingerConsumer.startSyntetisering(syntetiserNavmeldingerRequest);

        return response.getBody();
    }
}