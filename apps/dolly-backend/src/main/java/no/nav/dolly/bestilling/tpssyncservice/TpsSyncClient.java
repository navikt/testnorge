package no.nav.dolly.bestilling.tpssyncservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static no.nav.dolly.domain.CommonKeysAndUtils.PDL_TPS_CREATE_ENV;
import static no.nav.dolly.domain.CommonKeysAndUtils.isPdlTpsCreate;

@Slf4j
@Service
@Order(6)
@RequiredArgsConstructor
public class TpsSyncClient implements ClientRegister {

    private static final long TIMEOUT = 10 * 1000L;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isPdlTpsCreate(bestilling.getEnvironments())) {

            var startTime = System.currentTimeMillis();
            List<TpsIdentStatusDTO> response;

            do {
                response = tpsMessagingConsumer.getIdenter(List.of(dollyPerson.getHovedperson()),
                        new ArrayList<>(PDL_TPS_CREATE_ENV));

            } while (response.stream().anyMatch(elem -> elem.getMiljoer().isEmpty()) &&
                    startTime + TIMEOUT < System.currentTimeMillis());

            log.info("Synk mot TPS tok {} ms", System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public void release(List<String> identer) {
        // Ikke relevant
    }
}
