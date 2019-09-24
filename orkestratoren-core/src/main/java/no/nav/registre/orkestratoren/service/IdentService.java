package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.AaregSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.ArenaConsumer;
import no.nav.registre.orkestratoren.consumer.rs.InstSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.PoppSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.provider.rs.responses.SletteFraAvspillerguppeResponse;
import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
public class IdentService {

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

        slettedeIdenterResponse.getTpsfStatus().setSlettedeMeldingIderFraTpsf(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(avspillergruppeId, identer));
        slettedeIdenterResponse.setInstStatus(instSyntConsumer.slettIdenterFraInst(identer));
        slettedeIdenterResponse.setSigrunStatus(poppSyntConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer));
        slettedeIdenterResponse.setAaregStatus(aaregSyntConsumer.slettIdenterFraAaregstub(identer));
        // TODO: Fiks arena og legg inn denne igjen
        // slettedeIdenterResponse.setArenaForvalterStatus(arenaConsumer.slettIdenter(miljoe, identer));

        return slettedeIdenterResponse;
    }

    public List<String> synkroniserMedTps(Long avspillergruppeId, String miljoe) {
        return hodejegerenConsumer.getIdenterSomIkkeErITps(avspillergruppeId, miljoe);
    }
}
