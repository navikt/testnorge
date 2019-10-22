package no.nav.registre.tss.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static no.nav.registre.tss.domain.Person.MAX_ALDER;
import static no.nav.registre.tss.domain.Person.MIN_ALDER;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.TssSyntetisererenConsumer;
import no.nav.registre.tss.consumer.rs.response.TssMessage;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.domain.Samhandler;
import no.nav.registre.tss.provider.rs.request.SyntetiserTssRequest;
import no.nav.registre.tss.utils.RutineUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private final HodejegerenConsumer hodejegerenConsumer;

    private final TssSyntetisererenConsumer tssSyntetisererenConsumer;

    private final JmsService jmsService;

    public List<Person> hentIdenter(SyntetiserTssRequest syntetiserTssRequest) {
        Map<String, JsonNode> identerMedStatusQuo = hodejegerenConsumer.getStatusQuo(
                syntetiserTssRequest.getAvspillergruppeId(),
                syntetiserTssRequest.getMiljoe(),
                syntetiserTssRequest.getAntallNyeIdenter(),
                MIN_ALDER,
                MAX_ALDER);

        List<Person> identer = new ArrayList<>(identerMedStatusQuo.size());
        for (Map.Entry<String, JsonNode> entry : identerMedStatusQuo.entrySet()) {
            JsonNode jsonNode = entry.getValue();
            identer.add(new Person(
                    entry.getKey(),
                    jsonNode.findValue("gjeldendePersonnavn").asText())
            );
        }
        return identer;
    }

    public List<String> opprettSyntetiskeTssRutiner(List<Samhandler> samhandlere) {
        Map<String, List<TssMessage>> syntetiskeTssRutiner = tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(samhandlere);
        if (syntetiskeTssRutiner == null) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Kunne ikke hente rutiner fra synt");
        }
        log.info("Størrelse på rutiner hentet fra synt: {}", syntetiskeTssRutiner.size());
        List<String> flatfiler = new ArrayList<>(syntetiskeTssRutiner.values().size());

        for (var entry : syntetiskeTssRutiner.entrySet()) {
            List<TssMessage> rutiner = entry.getValue();
            if (rutiner == null) {
                log.warn("Fikk ingen rutine for ident: {}", entry.getKey());
                continue;
            }
            flatfiler.add(RutineUtil.opprettFlatfil(rutiner));
        }

        return flatfiler;
    }

    public void sendTilTss(List<String> tssRutiner, String miljoe) {
        String koeNavn = jmsService.hentKoeNavnAjour(miljoe);
        jmsService.sendTilTss(tssRutiner, koeNavn);
    }
}
