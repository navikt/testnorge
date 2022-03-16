package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingClient;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrdreService {

    private static final String IKKE_FUNNET = "Testperson med ident %s ble ikke funnet";
    private final IdentRepository identRepository;
    private final PdlDataConsumer pdlDataConsumer;
    private final TpsMessagingClient tpsMessagingClient;

    @Transactional(readOnly = true)
    public RsWhereAmI sendOrdre(String ident) {

        var testident = identRepository.findByIdent(ident)
                        .orElseThrow(() -> new NotFoundException(String.format(IKKE_FUNNET, ident)));

        var progress = BestillingProgress.builder()
                .ident(ident)
                .master(testident.getMaster())
        var response = pdlDataConsumer.sendOrdre(ident, testident.isTpsf());
        tpsMessagingClient.gjenopprett(new RsDollyUtvidetBestilling(),
                DollyPerson.builder()
                        .hovedperson(ident)
                .build(),
                BestillingProgress.builder().build(), false);

    }
}
