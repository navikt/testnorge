package no.nav.registre.bisys.service;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.BisysApplication;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.HodejegerenConsumer;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.rs.responses.relasjon.Relasjon;
import no.nav.registre.bisys.consumer.rs.responses.relasjon.RelasjonsResponse;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.TestnorgeToBisysMapper;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiFatteVedtakConsumer;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiSoknadConsumer;
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
    private BisysSyntetisererenConsumer syntetisererenConsumer;

    @Autowired
    private BisysUiSupport navigationSupport;

    @Autowired
    private BisysUiSoknadConsumer soknadConsumer;

    @Autowired
    private BisysUiFatteVedtakConsumer fatteVedtakConsumer;

    @Autowired
    private BisysRequestAugments bisysRequestAugments;

    private TestnorgeToBisysMapper testnorgeToBisysMapper = Mappers.getMapper(TestnorgeToBisysMapper.class);

    public List<SyntetisertBidragsmelding> generateBidragsmeldinger(
            SyntetiserBisysRequest syntetiserBisysRequest) {

        List<String> identerMedFoedselsmelding = hodejegerenConsumer.finnFoedteIdenter(syntetiserBisysRequest.getAvspillergruppeId());
        List<Barn> utvalgteIdenter = selectValidUids(syntetiserBisysRequest.getAntallNyeIdenter(),
                identerMedFoedselsmelding, syntetiserBisysRequest.getMiljoe());

        if (utvalgteIdenter.size() < syntetiserBisysRequest.getAntallNyeIdenter()) {
            log.warn("Fant ikke nok identer registrert med mor og far. Oppretter {} bidragsmelding(er).",
                    utvalgteIdenter.size());
        }

        List<SyntetisertBidragsmelding> bidragsmeldinger = syntetisererenConsumer.getSyntetiserteBidragsmeldinger(utvalgteIdenter.size());

        setRelationsInBidragsmeldinger(utvalgteIdenter, bidragsmeldinger);

        return bidragsmeldinger;
    }

    public void processBidragsmeldinger(List<SyntetisertBidragsmelding> bidragsmeldinger)
            throws BidragRequestProcessingException {
        BisysApplication bisys = null;

        if (bidragsmeldinger != null && bidragsmeldinger.size() > 0) {
            bisys = navigationSupport.logon();
        } else {
            log.warn("Ingen bidragsmeldinger mottatt, avbryter prosessering.");
            return;
        }

        for (SyntetisertBidragsmelding bidragsmelding : bidragsmeldinger) {
            try {
                SynthesizedBidragRequest request = testnorgeToBisysMapper.testnorgeToBisys(bidragsmelding, bisysRequestAugments);
                soknadConsumer.openOrCreateSoknad(bisys, request);
                fatteVedtakConsumer.runFatteVedtak(bisys, request);
            } catch (BidragRequestProcessingException e) {
                log.warn(
                        "En feil oppstod under prosessering av bidragsmelding for barn {}. Fortsetter med prossesering av neste melding.",
                        bidragsmelding.getBarnetsFnr());
            }
        }
    }

    private List<Barn> selectValidUids(int antallIdenter, List<String> identerMedFoedselsmelding,
            String miljoe) {
        List<Barn> utvalgteIdenter = new ArrayList<>();

        for (String ident : identerMedFoedselsmelding) {
            RelasjonsResponse relasjonsResponse = hodejegerenConsumer.hentRelasjonerTilIdent(ident, miljoe);
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

    private void setRelationsInBidragsmeldinger(List<Barn> utvalgteIdenter,
            List<SyntetisertBidragsmelding> bidragsmeldinger) {
        for (SyntetisertBidragsmelding bidragsMelding : bidragsmeldinger) {
            Barn barn = utvalgteIdenter.remove(0);
            bidragsMelding.setBarnetsFnr(barn.getFnr());
            bidragsMelding.setBidragsmottaker(barn.getMorFnr());
            bidragsMelding.setBidragspliktig(barn.getFarFnr());
        }
    }
}
