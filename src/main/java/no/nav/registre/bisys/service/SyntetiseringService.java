package no.nav.registre.bisys.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.ui.BisysUiConsumer;
import no.nav.registre.bisys.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;
import no.nav.registre.bisys.service.utils.Barn;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.Relasjon;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.RelasjonsResponse;

@Service
@Slf4j
public class SyntetiseringService {

    public static final String RELASJON_MOR = "MORA";
    public static final String RELASJON_FAR = "FARA";

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private BisysSyntetisererenConsumer bisysSyntetisererenConsumer;

    @Autowired
    private BisysUiConsumer bisysUiConsumer;

    public List<SyntetisertBidragsmelding> generateBidragsmeldinger(
            SyntetiserBisysRequest syntetiserBisysRequest) {

        List<String> identerMedFoedselsmelding = finnFoedteIdenter(syntetiserBisysRequest.getAvspillergruppeId());
        List<Barn> utvalgteIdenter = selectValidUids(
                syntetiserBisysRequest.getAntallNyeIdenter(),
                identerMedFoedselsmelding,
                syntetiserBisysRequest.getMiljoe());

        if (utvalgteIdenter.size() < syntetiserBisysRequest.getAntallNyeIdenter()) {
            log.warn(
                    "Fant ikke nok identer registrert med mor og far. Oppretter {} bidragsmelding(er).",
                    utvalgteIdenter.size());
        }

        List<SyntetisertBidragsmelding> bidragsmeldinger = bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(utvalgteIdenter.size());

        setRelationsInBidragsmeldinger(utvalgteIdenter, bidragsmeldinger);

        return bidragsmeldinger;
    }

    public void processBidragsmeldinger(List<SyntetisertBidragsmelding> bidragsmeldinger) {
        for (SyntetisertBidragsmelding bidragsmelding : bidragsmeldinger) {
            try {
                bisysUiConsumer.createVedtak(bidragsmelding);
            } catch (BidragRequestProcessingException e) {
                log.warn(
                        "En feil oppstod under prosessering av bidragsmelding for barn {}. Fortsetter med prossesering av neste melding.",
                        bidragsmelding.getBarnetsFnr());
            }
        }
    }

    private List<Barn> selectValidUids(
            int antallIdenter, List<String> identerMedFoedselsmelding, String miljoe) {
        List<Barn> utvalgteIdenter = new ArrayList<>();

        for (String ident : identerMedFoedselsmelding) {
            RelasjonsResponse relasjonsResponse = finnRelasjonerTilIdent(ident, miljoe);
            List<Relasjon> relasjoner = relasjonsResponse.getRelasjoner();

            String morFnr = "";
            String farFnr = "";

            for (Relasjon relasjon : relasjoner) {
                if (RELASJON_MOR.equals(relasjon.getTypeRelasjon())) {
                    morFnr = relasjon.getFnrRelasjon();
                } else if (RELASJON_FAR.equals(relasjon.getTypeRelasjon())) {
                    farFnr = relasjon.getFnrRelasjon();
                }
            }

            if (!morFnr.isEmpty() && !farFnr.isEmpty()) {
                utvalgteIdenter.add(Barn.builder().fnr(ident).morFnr(morFnr).farFnr(farFnr).build());
            }

            if (utvalgteIdenter.size() >= antallIdenter) {
                break;
            }
        }

        return utvalgteIdenter;
    }

    private void setRelationsInBidragsmeldinger(
            List<Barn> utvalgteIdenter, List<SyntetisertBidragsmelding> bidragsmeldinger) {
        for (SyntetisertBidragsmelding bidragsMelding : bidragsmeldinger) {
            Barn barn = utvalgteIdenter.remove(0);
            bidragsMelding.setBarnetsFnr(barn.getFnr());
            bidragsMelding.setBidragsmottaker(barn.getMorFnr());
            bidragsMelding.setBidragspliktig(barn.getFarFnr());
        }
    }

    @Timed(value = "bisys.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> finnFoedteIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId);
    }

    @Timed(value = "bisys.resource.latency", extraTags = { "operation", "hodejegeren" })
    public RelasjonsResponse finnRelasjonerTilIdent(String ident, String miljoe) {
        return hodejegerenConsumer.getRelasjoner(ident, miljoe);
    }
}
