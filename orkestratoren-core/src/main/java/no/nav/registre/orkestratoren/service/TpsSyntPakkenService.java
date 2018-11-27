package no.nav.registre.orkestratoren.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.StatusPaaAvspiltSkdMelding;

@Service
@Slf4j
public class TpsSyntPakkenService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    public AvspillingResponse produserOgSendSkdmeldingerTilTpsIMiljoer(Long skdMeldingGruppeId,
            String miljoe,
            Map<String, Integer> antallMeldingerPerEndringskode) {

        List<Long> ids = new ArrayList<>();
        try {
            ids.addAll(hodejegerenConsumer.startSyntetisering(new GenereringsOrdreRequest(skdMeldingGruppeId, miljoe, antallMeldingerPerEndringskode)));
        } catch (HttpStatusCodeException e) {
            try {
                JsonNode jsonNode = new ObjectMapper().readTree(e.getResponseBodyAsString()).get("ids");
                for (final JsonNode idNode : jsonNode) {
                    ids.add(idNode.asLong());
                }
            } catch (IOException ie) {
                log.warn("Kunne ikke hente id-er fra innholdet i exception fra Hodejegeren");
            }
            throw e;
        } finally {
            if (ids.isEmpty()) {
                StatusPaaAvspiltSkdMelding status = new StatusPaaAvspiltSkdMelding();
                status.setStatus("Noe feilet i hodejegeren. Ingen id-er kan sendes til TPS.");
                return new AvspillingResponse().addStatusFraFeilendeMeldinger(status);
            } else {
                return tpsfConsumer.sendSkdmeldingerTilTps(skdMeldingGruppeId, new SendToTpsRequest(miljoe, ids));
            }
        }
    }
}