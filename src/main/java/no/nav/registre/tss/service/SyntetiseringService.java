package no.nav.registre.tss.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.EregMapperConsumer;
import no.nav.registre.tss.consumer.rs.TssSyntetisererenConsumer;
import no.nav.registre.tss.consumer.rs.request.EregMapperRequest;
import no.nav.registre.tss.consumer.rs.request.Knytning;
import no.nav.registre.tss.consumer.rs.request.Navn;
import no.nav.registre.tss.consumer.rs.response.TssSyntMessage;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.domain.Samhandler;
import no.nav.registre.tss.domain.TssType;
import no.nav.registre.tss.provider.rs.request.SyntetiserTssRequest;
import no.nav.registre.tss.utils.RutineUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final int MIN_ALDER = 25;
    private static final int MAX_ALDER = 70;

    private final HodejegerenConsumer hodejegerenConsumer;

    private final TssSyntetisererenConsumer tssSyntetisererenConsumer;

    private final JmsService jmsService;

    private final EregMapperConsumer eregMapperConsumer;

    @Value("${testnorge.ereg.enhet.as}")
    private String ASEnhet;

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
        Map<String, List<TssSyntMessage>> syntetiskeTssRutiner = tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(samhandlere);
        List<String> flatfiler = new ArrayList<>(syntetiskeTssRutiner.values().size());
        //TODO: legg til ereg info
        for (List<TssSyntMessage> rutiner : syntetiskeTssRutiner.values()) {
            flatfiler.add(RutineUtil.opprettFlatfil(rutiner));
        }

        return flatfiler;
    }

    public void sendTilTss(List<String> tssRutiner, String miljoe) {
        String koeNavn = jmsService.hentKoeNavnAjour(miljoe);
        jmsService.sendTilTss(tssRutiner, koeNavn);
    }

    private EregMapperRequest opprettEregRequest(TssSyntMessage melding) {
        return EregMapperRequest.builder()
                .knytninger(Collections.singletonList(Knytning.builder().orgnr(ASEnhet).build()))
                .navn(Navn.builder()
                        .navneListe(Collections.singletonList(melding.getNavn().split(" ")[0] + TssType.valueOf(melding.getKodeSamhType()).beskrivelse))
                        .build())
                .orgnr(melding.getIdOff())
                .build();
    }

    private String hentEttOrgnr() {
        List<String> orgnr = eregMapperConsumer.hentOrgnr(1);
        if (orgnr.size() == 1) {
            return orgnr.get(0);
        }
        throw new RuntimeException("Kunne ikke generere orgnummer");
    }
}
