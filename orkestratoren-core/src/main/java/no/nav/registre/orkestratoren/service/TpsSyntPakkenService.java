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
import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
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
    private HodejegerenConsumer hodejegerenConsumer;

    private static final String MULIG_LAGRET_MEN_KANSKJE_IKKE_SENDT_MELDING = "Noe feilet i TPSF-sendSkdmeldingerTilTps. "
            + "Følgende id-er ble lagret i TPSF avspillergruppe {}, men er trolig ikke sendt til TPS: {}";

    public SkdMeldingerTilTpsRespons produserOgSendSkdmeldingerTilTpsIMiljoer(Long skdMeldingGruppeId,
            String miljoe,
            Map<String, Integer> antallMeldingerPerEndringskode) {

        List<Long> ids = new ArrayList<>();
        SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = null;
        HttpStatusCodeExceptionContainer httpStatusCodeExceptionContainer = new HttpStatusCodeExceptionContainer();
        try {
            ids.addAll(hodejegerenConsumer.startSyntetisering(new GenereringsOrdreRequest(skdMeldingGruppeId, miljoe, antallMeldingerPerEndringskode)));
        } catch (HttpStatusCodeException e) {
            ids.addAll(extractIdsFromResponseBody(e));
            httpStatusCodeExceptionContainer.addException(e);
        }
        if (ids.isEmpty()) {
            StatusPaaAvspiltSkdMelding status = new StatusPaaAvspiltSkdMelding();
            status.setStatus("Hodejegeren returnerte uten å ha lagret noen melding i TPSF. Ingen id-er å sende til TPS.");
            skdMeldingerTilTpsRespons = new SkdMeldingerTilTpsRespons().addStatusFraFeilendeMeldinger(status);
        } else {
            try {
                skdMeldingerTilTpsRespons = tpsfConsumer.sendSkdmeldingerTilTps(skdMeldingGruppeId, new SendToTpsRequest(miljoe, ids));
                skdMeldingerTilTpsRespons.setTpsfIds(ids);
                log.info("{} id-er ble sendt til TPS.", ids.size());
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode().is5xxServerError()) {
                    log.error(e.getResponseBodyAsString(), e);
                }
                log.warn(MULIG_LAGRET_MEN_KANSKJE_IKKE_SENDT_MELDING, skdMeldingGruppeId, createListOfRangesFromIds(ids));
                httpStatusCodeExceptionContainer.addException(e);
                httpStatusCodeExceptionContainer.addFeilmeldingBeskrivelse(MessageFormatter.format(MULIG_LAGRET_MEN_KANSKJE_IKKE_SENDT_MELDING, skdMeldingGruppeId, ids.toString()).getMessage());
            }
        }
        if (!httpStatusCodeExceptionContainer.getNestedExceptions().isEmpty()) {
            filterStackTraceOnNavSpecificItems(httpStatusCodeExceptionContainer);
            throw httpStatusCodeExceptionContainer;
        }
        return skdMeldingerTilTpsRespons;
    }
}