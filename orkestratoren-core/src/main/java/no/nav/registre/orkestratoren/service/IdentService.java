package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeAaregConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaConsumer;
import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInstConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSigrunConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.provider.rs.responses.SletteFraAvspillerguppeResponse;
import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;

@Service
public class IdentService {

    private static final String TESTDATAEIER = "orkestratoren";

    @Autowired
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Autowired
    private TestnorgeInstConsumer testnorgeInstConsumer;

    @Autowired
    private TestnorgeSigrunConsumer testnorgeSigrunConsumer;

    @Autowired
    private TestnorgeAaregConsumer testnorgeAaregConsumer;

    @Autowired
    private TestnorgeArenaConsumer testnorgeArenaConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    public SlettedeIdenterResponse slettIdenterFraAdaptere(Long avspillergruppeId, String miljoe, String testdataEier, List<String> identer) {
        SlettedeIdenterResponse slettedeIdenterResponse = SlettedeIdenterResponse.builder()
                .tpsfStatus(SletteFraAvspillerguppeResponse.builder()
                        .build())
                .build();

        slettedeIdenterResponse.getTpsfStatus().setSlettedeMeldingIderFraTpsf(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(avspillergruppeId, Collections.singletonList(miljoe), identer));
        slettedeIdenterResponse.setInstStatus(testnorgeInstConsumer.slettIdenterFraInst(identer));
        slettedeIdenterResponse.setSigrunStatus(testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer));
        slettedeIdenterResponse.setAaregStatus(testnorgeAaregConsumer.slettIdenterFraAaregstub(identer));
        // TODO: Fiks arena og legg inn denne igjen
        // slettedeIdenterResponse.setArenaForvalterStatus(arenaConsumer.slettIdenter(miljoe, identer));

        return slettedeIdenterResponse;
    }

    public SlettedeIdenterResponse synkroniserMedTps(Long avspillergruppeId, String miljoe) {
        SlettedeIdenterResponse slettedeIdenterResponse = SlettedeIdenterResponse.builder().build();
        List<String> identerSomIkkeErITps = hodejegerenConsumer.hentIdenterSomIkkeErITps(avspillergruppeId, miljoe);
        if (!identerSomIkkeErITps.isEmpty()) {
            slettedeIdenterResponse = slettIdenterFraAdaptere(
                    avspillergruppeId,
                    miljoe,
                    TESTDATAEIER,
                    identerSomIkkeErITps
            );
        }
        return slettedeIdenterResponse;
    }

    public SlettedeIdenterResponse fjernKolliderendeIdenter(Long avspillergruppeId, String miljoe) {
        SlettedeIdenterResponse slettedeIdenterResponse = SlettedeIdenterResponse.builder().build();
        List<String> identerSomKolliderer = hodejegerenConsumer.hentIdenterSomKollidererITps(avspillergruppeId);
        if (!identerSomKolliderer.isEmpty()) {
            slettedeIdenterResponse = slettIdenterFraAdaptere(
                    avspillergruppeId,
                    miljoe,
                    TESTDATAEIER,
                    identerSomKolliderer
            );
        }
        return slettedeIdenterResponse;
    }
}
