package no.nav.registre.orkestratoren.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.orkestratoren.consumer.rs.InstSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.PoppSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.provider.rs.responses.SletteFraAvspillerguppeResponse;
import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;

@Service
public class IdentService {

    @Autowired
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Autowired
    private InstSyntConsumer instSyntConsumer;

    @Autowired
    private PoppSyntConsumer poppSyntConsumer;

    public SlettedeIdenterResponse slettIdenterFraAdaptere(Long avspillergruppeId, String miljoe, String testdataEier, List<String> identer) {
        SlettedeIdenterResponse slettedeIdenterResponse = SlettedeIdenterResponse.builder()
                .tpsfStatus(SletteFraAvspillerguppeResponse.builder()
                        .build())
                .build();

        slettedeIdenterResponse.getTpsfStatus().setSlettedeMeldingIderFraTpsf(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(avspillergruppeId, identer));
        slettedeIdenterResponse.setInstStatus(instSyntConsumer.slettIdenterFraInst(identer));
        slettedeIdenterResponse.setSigrunStatus(poppSyntConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer));

        return slettedeIdenterResponse;
    }
}
