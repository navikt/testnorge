package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.AaregSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.ArenaConsumer;
import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
import no.nav.registre.orkestratoren.consumer.rs.InstSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.PoppSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.provider.rs.responses.SletteFraAvspillerguppeResponse;
import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;

@Service
public class IdentService {

    private static final String TESTDATAEIER = "orkestratoren";

    @Autowired
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Autowired
    private InstSyntConsumer instSyntConsumer;

    @Autowired
    private PoppSyntConsumer poppSyntConsumer;

    @Autowired
    private AaregSyntConsumer aaregSyntConsumer;

    @Autowired
    private ArenaConsumer arenaConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    public SlettedeIdenterResponse slettIdenterFraAdaptere(Long avspillergruppeId, String miljoe, String testdataEier, List<String> identer) {
        SlettedeIdenterResponse slettedeIdenterResponse = SlettedeIdenterResponse.builder()
                .tpsfStatus(SletteFraAvspillerguppeResponse.builder()
                        .build())
                .build();

        slettedeIdenterResponse.getTpsfStatus().setSlettedeMeldingIderFraTpsf(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(avspillergruppeId, Collections.singletonList(miljoe), identer));
        slettedeIdenterResponse.setInstStatus(instSyntConsumer.slettIdenterFraInst(identer));
        slettedeIdenterResponse.setSigrunStatus(poppSyntConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer));
        slettedeIdenterResponse.setAaregStatus(aaregSyntConsumer.slettIdenterFraAaregstub(identer));
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
