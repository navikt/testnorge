package no.nav.registre.testnorge.arena.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.InnsatsArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.EndreInnsatsbehovRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyEndreInnsatsbehov;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class InnsatsService {

    private final InnsatsArenaForvalterConsumer innsatsArenaForvalterConsumer;

    private static final String IARBS_HOVEDMAAL = "BEHOLDEA";


    public void endreTilFormidlingsgruppeIarbs(String personident, String miljoe, Kvalifiseringsgrupper kvalifiseringsgruppe) {
        log.info("Endrer formidlingsgruppe til IARBS for ident: " + personident);
        var request = EndreInnsatsbehovRequest.builder()
                .personident(personident)
                .miljoe(miljoe)
                .nyeEndreInnsatsbehov(Collections.singletonList(NyEndreInnsatsbehov.builder()
                        .kvalifiseringsgruppe(kvalifiseringsgruppe)
                        .hovedmaal(IARBS_HOVEDMAAL)
                        .build()))
                .build();
        innsatsArenaForvalterConsumer.endreInnsatsbehov(request);
    }
}
