package no.nav.registre.inntekt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektSyntConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektstubConsumer;
import no.nav.registre.inntekt.domain.IdentMedData;
import no.nav.registre.inntekt.domain.InntektSaveInHodejegerenRequest;
import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Slf4j
@Service
public class SyntetiseringService {

    private static final int MINIMUM_ALDER = 13;
    private static final String ARENA_INNTEKT_NAME = "inntekt";

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private InntektSyntConsumer inntektSyntConsumer;

    @Autowired
    private InntektstubConsumer inntektstubConsumer;

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    public Map<String, List<RsInntekt>> startSyntetisering(SyntetiseringsRequest syntetiseringsRequest) {

        List<String> identer = hentLevendeIdenterOverAlder(syntetiseringsRequest.getAvspillergruppeId(), MINIMUM_ALDER);

        if (!identer.isEmpty()) {
            // >> 1 er samme som å dele på 2 (Java liker ikke float division i int context
            identer = identer.subList(0, (identer.size() + 1 >> 1));
        }

        if (identer.isEmpty()) {
            log.warn("Ingen identer å opprette meldinger på");
            return null;
        }

        Map<String, List<RsInntekt>> inntektsmeldinger = getInntektsmeldingerFraSynt(identer);

        if (inntektsmeldinger == null) {
            log.warn("Ingen inntektsmeldinger fra synt");
            return null;
        }

        Map<String, List<RsInntekt>> gyldigeMeldinger = fjernTommeMeldinger(inntektsmeldinger);

        Map<String, List<RsInntekt>> feiledeInntektsmeldinger = inntektstubConsumer.leggInntekterIInntektstub(gyldigeMeldinger);

        if (!feiledeInntektsmeldinger.isEmpty()) {
            log.warn("Kunne ikke opprette inntekt på følgende identer: {}", feiledeInntektsmeldinger.keySet());

            for (Map.Entry<String, List<RsInntekt>> feilet : feiledeInntektsmeldinger.entrySet()) {
                gyldigeMeldinger.remove(feilet.getKey());
            }
        }

        List<IdentMedData> identerMedData = new ArrayList<>(gyldigeMeldinger.size());
        for (Map.Entry<String, List<RsInntekt>> personInfo : gyldigeMeldinger.entrySet()) {
            identerMedData.add(new IdentMedData(personInfo.getKey(), personInfo.getValue()));
        }

        InntektSaveInHodejegerenRequest hodejegerenRequests = new InntektSaveInHodejegerenRequest(ARENA_INNTEKT_NAME, identerMedData);
        List<String> lagredeIdenter = hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequests);

        if (lagredeIdenter.size() < identerMedData.size()) {
            List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
            for (IdentMedData ident : identerMedData) {
                identerSomIkkeBleLagret.add(ident.getId());
            }
            identerSomIkkeBleLagret.removeAll(lagredeIdenter);
            log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
        }

        return feiledeInntektsmeldinger;
    }

    private List<String> hentLevendeIdenterOverAlder(Long avspillergruppeId, int minimumAlder) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder);
    }

    private Map<String, List<RsInntekt>> getInntektsmeldingerFraSynt(List<String> identer) {
        Map<String, List<RsInntekt>> inntektsmeldinger = inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(identer);
        if (inntektsmeldinger == null) {
            log.warn("syntetiserte meldinger feilet");
            return null;
        }
        if (inntektsmeldinger.isEmpty()) {
            log.info("Ingen intektsmeldinger syntetisert, identene kan ha eksisterende meldinger for denne måned. \nIdenter ønsket å genere melding på {}", identer);
            return null;
        }
        return inntektsmeldinger;
    }

    private Map<String, List<RsInntekt>> fjernTommeMeldinger(Map<String, List<RsInntekt>> inntektsmeldinger) {
        inntektsmeldinger.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
        if (inntektsmeldinger.isEmpty()) {
            log.info("Ingen syntetiserte meldinger å legge på inntektstub, returnerer");
        }
        return inntektsmeldinger;
    }
}
