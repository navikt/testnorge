package no.nav.dolly.bestilling.dokarkiv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivClient implements ClientRegister {

    private final DokarkivConsumer dokarkivConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

    }

    @Override
    public void release(List<String> identer) {

    }
}
