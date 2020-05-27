package no.nav.registre.frikort.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.FrikortSyntetisererenConsumer;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private final FrikortSyntetisererenConsumer frikortSyntetisererenConsumer;
    private final KonverteringService konverteringService;

    public List<String> hentSyntetiskeEgenandelerSomXML(Map<String, Integer> request) throws JAXBException {
        var egenandeler = frikortSyntetisererenConsumer.hentSyntetiskeEgenandelerFraSyntRest(request);

        return konverteringService.konverterEgenandelerTilXmlString(egenandeler);
    }

}
