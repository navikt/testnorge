package no.nav.registre.bisys.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.HodejegerenConsumer;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.rs.responses.relasjon.Relasjon;
import no.nav.registre.bisys.consumer.rs.responses.relasjon.RelasjonsResponse;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;
import no.nav.registre.bisys.service.utils.Barn;

@Service
@Slf4j
public class SyntetiseringService {

    public static final String RELASJON_MOR = "MORA";
    public static final String RELASJON_FAR = "FARA";

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private BisysSyntetisererenConsumer bisysSyntetisererenConsumer;

    public List<SyntetisertBidragsmelding> genererBidragsmeldinger(SyntetiserBisysRequest syntetiserBisysRequest) {
        List<String> identerMedFoedselsmelding = hodejegerenConsumer.finnFoedteIdenter(syntetiserBisysRequest.getAvspillergruppeId());
        List<Barn> utvalgteIdenter = velgUtGyldigeIdenter(syntetiserBisysRequest.getAntallNyeIdenter(), identerMedFoedselsmelding, syntetiserBisysRequest.getMiljoe());

        if (utvalgteIdenter.size() < syntetiserBisysRequest.getAntallNyeIdenter()) {
            log.warn("Fant ikke nok identer registrert med mor og far. Oppretter {} bidragsmelding(er).", utvalgteIdenter.size());
        }

        List<SyntetisertBidragsmelding> bidragsmeldinger = bisysSyntetisererenConsumer.getSyntetiserteBidragsmeldinger(utvalgteIdenter.size());

        setRelasjonerIBidragsmeldinger(utvalgteIdenter, bidragsmeldinger);

        return bidragsmeldinger;
    }

    private List<Barn> velgUtGyldigeIdenter(int antallIdenter, List<String> identerMedFoedselsmelding, String miljoe) {
        List<Barn> utvalgteIdenter = new ArrayList<>();

        for (String ident : identerMedFoedselsmelding) {
            RelasjonsResponse relasjonsResponse = hodejegerenConsumer.hentRelasjonerTilIdent(ident, miljoe);
            List<Relasjon> relasjoner = relasjonsResponse.getRelasjoner();

            String morFnr = "";
            String farFnr = "";

            for (Relasjon relasjon : relasjoner) {
                if (relasjon.getTypeRelasjon().equals(RELASJON_MOR)) {
                    morFnr = relasjon.getFnrRelasjon();
                } else if (relasjon.getTypeRelasjon().equals(RELASJON_FAR)) {
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

    private void setRelasjonerIBidragsmeldinger(List<Barn> utvalgteIdenter, List<SyntetisertBidragsmelding> bidragsmeldinger) {
        for (SyntetisertBidragsmelding bidragsMelding : bidragsmeldinger) {
            Barn barn = utvalgteIdenter.remove(0);
            bidragsMelding.setBarnetsFnr(barn.getFnr());
            bidragsMelding.setBidragsmottaker(barn.getMorFnr());
            bidragsMelding.setBidragspliktig(barn.getFarFnr());
        }
    }
}
