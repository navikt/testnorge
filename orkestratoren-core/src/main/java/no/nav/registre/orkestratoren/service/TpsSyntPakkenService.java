package no.nav.registre.orkestratoren.service;

import static no.nav.registre.orkestratoren.utils.ExceptionUtils.createListOfRangesFromIds;
import static no.nav.registre.orkestratoren.utils.ExceptionUtils.extractIdsFromResponseBody;
import static no.nav.registre.orkestratoren.utils.ExceptionUtils.filterStackTraceOnNavSpecificItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.consumer.rs.response.StatusPaaAvspiltSkdMelding;
import no.nav.registre.orkestratoren.exceptions.HttpStatusCodeExceptionContainer;

@Service
@Slf4j
public class TpsSyntPakkenService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    private static final String MULIG_LAGRET_MEN_KANSKJE_IKKE_SENDT_MELDING = "Noe feilet i TPSF-sendSkdmeldingerTilTps. "
            + "Følgende id-er ble lagret i TPSF avspillergruppe {}, men er trolig ikke sendt til TPS: {}";

    public SkdMeldingerTilTpsRespons produserOgSendSkdmeldingerTilTpsIMiljoer(Long avspillergruppeId,
            String miljoe,
            Map<String, Integer> antallMeldingerPerEndringskode) {

        List<Long> ids = new ArrayList<>();
        SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = null;
        HttpStatusCodeExceptionContainer httpStatusCodeExceptionContainer = new HttpStatusCodeExceptionContainer();
        try {
            ids.addAll(testnorgeSkdConsumer.startSyntetisering(new GenereringsOrdreRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskode)));
        } catch (HttpStatusCodeException e) {
            ids.addAll(extractIdsFromResponseBody(e));
            httpStatusCodeExceptionContainer.addException(e);
            httpStatusCodeExceptionContainer.addFeilmeldingBeskrivelse(e.getResponseBodyAsString());
        }
        if (ids.isEmpty()) {
            StatusPaaAvspiltSkdMelding status = new StatusPaaAvspiltSkdMelding();
            status.setStatus("Testnorge-Skd returnerte uten å ha lagret noen melding i TPSF. Ingen id-er å sende til TPS.");
            skdMeldingerTilTpsRespons = new SkdMeldingerTilTpsRespons().addStatusFraFeilendeMeldinger(status);
        } else {
            try {
                skdMeldingerTilTpsRespons = tpsfConsumer.sendSkdmeldingerTilTps(avspillergruppeId, new SendToTpsRequest(miljoe, ids));
                skdMeldingerTilTpsRespons.setTpsfIds(ids);
                log.info("{} id-er ble sendt til TPS.", ids.size());
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode().is5xxServerError()) {
                    log.error(e.getResponseBodyAsString(), e);
                }
                log.warn(MULIG_LAGRET_MEN_KANSKJE_IKKE_SENDT_MELDING, avspillergruppeId, createListOfRangesFromIds(ids));
                httpStatusCodeExceptionContainer.addException(e);
                httpStatusCodeExceptionContainer.addFeilmeldingBeskrivelse(MessageFormatter.format(MULIG_LAGRET_MEN_KANSKJE_IKKE_SENDT_MELDING, avspillergruppeId, ids.toString()).getMessage());
            }
        }
        if (!httpStatusCodeExceptionContainer.getNestedExceptions().isEmpty()) {
            filterStackTraceOnNavSpecificItems(httpStatusCodeExceptionContainer);
            throw httpStatusCodeExceptionContainer;
        }
        return skdMeldingerTilTpsRespons;
    }
}