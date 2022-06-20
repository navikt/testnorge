package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInstConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSigrunConsumer;
import no.nav.registre.orkestratoren.provider.rs.responses.SletteFraAvspillerguppeResponse;
import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
@RequiredArgsConstructor
public class IdentService {

    private static final String TESTDATAEIER = "orkestratoren";

    private final TestnorgeInstConsumer testnorgeInstConsumer;

    private final TestnorgeSigrunConsumer testnorgeSigrunConsumer;

    private final HodejegerenConsumer hodejegerenConsumer;

    public SlettedeIdenterResponse slettIdenterFraAdaptere(
            String miljoe,
            String testdataEier,
            List<String> identer
    ) {
        var slettedeIdenterResponse = SlettedeIdenterResponse.builder()
                .tpsfStatus(SletteFraAvspillerguppeResponse.builder()
                        .build())
                .build();

        slettedeIdenterResponse.setInstStatus(testnorgeInstConsumer.slettIdenterFraInst(identer));
        slettedeIdenterResponse.setSigrunStatus(testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer));

        return slettedeIdenterResponse;
    }

    public SlettedeIdenterResponse synkroniserMedTps(
            Long avspillergruppeId,
            String miljoe
    ) {
        var slettedeIdenterResponse = SlettedeIdenterResponse.builder().build();
        var identerSomIkkeErITps = hodejegerenConsumer.getIdenterSomIkkeErITps(avspillergruppeId, miljoe);
        if (!identerSomIkkeErITps.isEmpty()) {
            slettedeIdenterResponse = slettIdenterFraAdaptere(
                    miljoe,
                    TESTDATAEIER,
                    identerSomIkkeErITps
            );
        }
        return slettedeIdenterResponse;
    }

    public SlettedeIdenterResponse fjernKolliderendeIdenter(
            Long avspillergruppeId,
            String miljoe
    ) {
        var slettedeIdenterResponse = SlettedeIdenterResponse.builder().build();
        var identerSomKolliderer = hodejegerenConsumer.getIdenterSomKolliderer(avspillergruppeId);
        if (!identerSomKolliderer.isEmpty()) {
            slettedeIdenterResponse = slettIdenterFraAdaptere(
                    miljoe,
                    TESTDATAEIER,
                    identerSomKolliderer
            );
        }
        return slettedeIdenterResponse;
    }
}
