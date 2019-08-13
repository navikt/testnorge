package no.nav.registre.sigrun.service;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.sigrun.IdentMedData;
import no.nav.registre.sigrun.SigrunSaveInHodejegerenRequest;
import no.nav.registre.sigrun.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.sigrun.consumer.rs.PoppSyntetisererenConsumer;
import no.nav.registre.sigrun.consumer.rs.SigrunStubConsumer;
import no.nav.registre.sigrun.consumer.rs.responses.SigrunSkattegrunnlagResponse;
import no.nav.registre.sigrun.provider.rs.requests.SyntetiserPoppRequest;
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

    public List<String> finnEksisterendeOgNyeIdenter(SyntetiserPoppRequest syntetiserPoppRequest) {
        List<String> eksisterendeIdenter = finnEksisterendeIdenter(syntetiserPoppRequest.getMiljoe());
        List<String> nyeIdenter = finnLevendeIdenter(syntetiserPoppRequest);

        int antallIdenterAlleredeIStub = 0;

        for (String ident : nyeIdenter) {
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

    public ResponseEntity genererPoppmeldingerOgSendTilSigrunStub(List<String> identer, String testdataEier, String miljoe) {
        List<Map<String, Object>> syntetiserteMeldinger = finnSyntetiserteMeldinger(identer);
        ResponseEntity response = sigrunStubConsumer.sendDataTilSigrunstub(syntetiserteMeldinger, testdataEier, miljoe);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<IdentMedData> identerMedData = new ArrayList<>(identer.size());
            if (identer.size() == syntetiserteMeldinger.size()) {
                for (int i = 0; i < identer.size(); i++) {
                    identerMedData.add(new IdentMedData(identer.get(i), Collections.singletonList(syntetiserteMeldinger.get(i))));
                }
            }
            SigrunSaveInHodejegerenRequest hodejegerenRequest = new SigrunSaveInHodejegerenRequest(SIGRUN_NAME, identerMedData);

            List<String> lagredeIdenter = hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequest);

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

    public SletteGrunnlagResponse slettSkattegrunnlagTilIdenter(List<String> identer, String testdataEier, String miljoe) {
        List<String> eksisterendeIdenter = finnEksisterendeIdenter(miljoe);
        eksisterendeIdenter.retainAll(identer);

        SletteGrunnlagResponse sletteGrunnlagResponse = SletteGrunnlagResponse.builder()
                .grunnlagSomIkkeKunneSlettes(new ArrayList<>())
                .grunnlagSomBleSlettet(new ArrayList<>())
                .identerMedGrunnlagFraAnnenTestdataEier(new ArrayList<>())
                .build();

        for (String ident : eksisterendeIdenter) {
            List<SigrunSkattegrunnlagResponse> skattegrunnlagTilIdent = sigrunStubConsumer.hentEksisterendeSkattegrunnlag(ident, miljoe);

            for (SigrunSkattegrunnlagResponse skattegrunnlag : skattegrunnlagTilIdent) {
                if (testdataEier.equals(skattegrunnlag.getTestdataEier())) {
                    ResponseEntity response = sigrunStubConsumer.slettEksisterendeSkattegrunnlag(skattegrunnlag, miljoe);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        sletteGrunnlagResponse.getGrunnlagSomBleSlettet().add(skattegrunnlag);
                    } else {
                        sletteGrunnlagResponse.getGrunnlagSomIkkeKunneSlettes().add(skattegrunnlag);
                        log.error("Kunne ikke slette skattegrunnlag {} til ident {}", skattegrunnlag.getGrunnlag(), ident);
                    }
                } else {
                    if (!sletteGrunnlagResponse.getIdenterMedGrunnlagFraAnnenTestdataEier().contains(ident)) {
                        sletteGrunnlagResponse.getIdenterMedGrunnlagFraAnnenTestdataEier().add(ident);
                    }
                }
            }
        }

        return sletteGrunnlagResponse;
    }

    private List<Map<String, Object>> finnSyntetiserteMeldinger(List<String> fnrs) {
        return poppSyntRestConsumer.hentPoppMeldingerFromSyntRest(fnrs);
    }

    private List<String> finnEksisterendeIdenter(String miljoe) {
        return sigrunStubConsumer.hentEksisterendePersonidentifikatorer(miljoe);
    }

    @Timed(value = "testnorge-sigrun.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> finnLevendeIdenter(SyntetiserPoppRequest syntetiserPoppRequest) {
        return hodejegerenConsumer.getLevende(
                syntetiserPoppRequest.getAvspillergruppeId(),
                syntetiserPoppRequest.getMiljoe(),
                syntetiserPoppRequest.getAntallNyeIdenter(),
                null);
    }
}
