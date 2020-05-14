package no.nav.registre.frikort.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.FrikortSyntetisererenConsumer;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SyntetiseringService {

    @Autowired
    private FrikortSyntetisererenConsumer frikortSyntetisererenConsumer;

    @Autowired
    private KonverteringService konverteringService;

    public List<String> hentSyntetiskeEgenandeler(Map<String, Integer> request) throws JAXBException {

        Map<String, List<SyntFrikortResponse>> egenandeler = frikortSyntetisererenConsumer.hentSyntetiskeEgenandelerFraSyntRest(request);
        return konverteringService.konverterFrikortTilXmlString(egenandeler);
    }

}
