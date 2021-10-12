package no.nav.registre.sigrun.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.sigrun.domain.IdentMedData;
import no.nav.registre.sigrun.domain.PoppSyntetisererenResponse;
import no.nav.registre.sigrun.domain.SigrunSaveInHodejegerenRequest;
import no.nav.registre.sigrun.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.sigrun.consumer.rs.PoppSyntetisererenConsumer;
import no.nav.registre.sigrun.consumer.rs.SigrunStubConsumer;
import no.nav.registre.sigrun.consumer.rs.responses.SigrunSkattegrunnlagResponse;
import no.nav.registre.sigrun.provider.rs.requests.SyntetiserSigrunRequest;
import no.nav.registre.sigrun.provider.rs.responses.SletteGrunnlagResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
@Slf4j
public class SigrunService {

    @Autowired
    private PoppSyntetisererenConsumer poppSyntRestConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    private static final String SIGRUN_NAME = "sigrun";

    public List<String> finnEksisterendeOgNyeIdenter(
            SyntetiserSigrunRequest syntetiserSigrunRequest,
            String testdataEier
    ) {
        var eksisterendeIdenter = finnEksisterendeIdenter(syntetiserSigrunRequest.getMiljoe(), testdataEier);
        var nyeIdenter = finnLevendeIdenter(syntetiserSigrunRequest);
        var antallIdenterAlleredeIStub = 0;

        for (var ident : nyeIdenter) {
            if (eksisterendeIdenter.contains(ident)) {
                antallIdenterAlleredeIStub++;
            } else {
                eksisterendeIdenter.add(ident);
            }
        }

        if (antallIdenterAlleredeIStub > 0) {
            log.info("{} av de nyutvalgte identene eksisterte allerede i sigrun-skd-stub.", antallIdenterAlleredeIStub);
        }

        return eksisterendeIdenter;
    }

    public ResponseEntity genererPoppmeldingerOgSendTilSigrunStub(
            List<String> identer,
            String testdataEier,
            String miljoe
    ) {
        var syntetiserteMeldinger = finnSyntetiserteMeldinger(identer);
        var response = sigrunStubConsumer.sendDataTilSigrunstub(syntetiserteMeldinger, testdataEier, miljoe);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<IdentMedData> identerMedData = new ArrayList<>(identer.size());

            fyllIdenterMedData(identer, syntetiserteMeldinger, identerMedData);

            var hodejegerenRequest = new SigrunSaveInHodejegerenRequest(SIGRUN_NAME, identerMedData);

            var lagredeIdenter = hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequest);

            if (lagredeIdenter.size() < identerMedData.size()) {
                List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
                for (IdentMedData ident : identerMedData) {
                    identerSomIkkeBleLagret.add(ident.getId());
                }
                identerSomIkkeBleLagret.removeAll(lagredeIdenter);
                log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
            }
        }
        return response;
    }

    private void fyllIdenterMedData(List<String> identer, List<PoppSyntetisererenResponse> syntetiserteMeldinger, List<IdentMedData> identerMedData) {
        for (var ident : identer) {
            var identMedData = IdentMedData.builder()
                    .id(ident)
                    .data(new ArrayList<>())
                    .build();
            identerMedData.add(identMedData);

            var syntetiserteMeldingerIterator = syntetiserteMeldinger.iterator();
            while (syntetiserteMeldingerIterator.hasNext()) {
                var melding = syntetiserteMeldingerIterator.next();
                var personidentifikator = melding.getPersonidentifikator();
                if (ident.equals(personidentifikator)) {
                    identMedData.getData().add(melding);
                    syntetiserteMeldingerIterator.remove();
                } else {
                    break; // vi kan breake her da meldingene ligger sortert etter personidentifikator
                }
            }
        }
    }

    public SletteGrunnlagResponse slettSkattegrunnlagTilIdenter(
            List<String> identer,
            String testdataEier,
            String miljoe
    ) {
        var eksisterendeIdenter = finnEksisterendeIdenter(miljoe, testdataEier);
        eksisterendeIdenter.retainAll(identer);

        var sletteGrunnlagResponse = SletteGrunnlagResponse.builder()
                .grunnlagSomIkkeKunneSlettes(new ArrayList<>())
                .grunnlagSomBleSlettet(new ArrayList<>())
                .identerMedGrunnlagFraAnnenTestdataEier(new ArrayList<>())
                .build();

        for (var ident : eksisterendeIdenter) {
            var skattegrunnlagTilIdent = sigrunStubConsumer.hentEksisterendeSkattegrunnlag(ident, miljoe);

            for (var skattegrunnlag : skattegrunnlagTilIdent) {
                if (testdataEier.equals(skattegrunnlag.getTestdataEier())) {
                    var response = sigrunStubConsumer.slettEksisterendeSkattegrunnlag(skattegrunnlag, miljoe);
                    fyllSletteGrunnlagResponse(sletteGrunnlagResponse, ident, skattegrunnlag, response);
                } else {
                    if (!sletteGrunnlagResponse.getIdenterMedGrunnlagFraAnnenTestdataEier().contains(ident)) {
                        sletteGrunnlagResponse.getIdenterMedGrunnlagFraAnnenTestdataEier().add(ident);
                    }
                }
            }
        }

        return sletteGrunnlagResponse;
    }

    private void fyllSletteGrunnlagResponse(SletteGrunnlagResponse sletteGrunnlagResponse, String ident, SigrunSkattegrunnlagResponse skattegrunnlag, ResponseEntity<?> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            sletteGrunnlagResponse.getGrunnlagSomBleSlettet().add(skattegrunnlag);
        } else {
            sletteGrunnlagResponse.getGrunnlagSomIkkeKunneSlettes().add(skattegrunnlag);
            log.error("Kunne ikke slette skattegrunnlag {} til ident {}", skattegrunnlag.getGrunnlag(), ident);
        }
    }

    public List<String> finnEksisterendeIdenter(
            String miljoe,
            String testdataEier
    ) {
        return sigrunStubConsumer.hentEksisterendePersonidentifikatorer(miljoe, testdataEier);
    }

    private List<PoppSyntetisererenResponse> finnSyntetiserteMeldinger(List<String> fnrs) {
        return poppSyntRestConsumer.hentPoppMeldingerFromSyntRest(fnrs);
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> finnLevendeIdenter(SyntetiserSigrunRequest syntetiserSigrunRequest) {
        return hodejegerenConsumer.getLevende(
                syntetiserSigrunRequest.getAvspillergruppeId(),
                syntetiserSigrunRequest.getMiljoe(),
                syntetiserSigrunRequest.getAntallNyeIdenter(),
                null);
    }
}
