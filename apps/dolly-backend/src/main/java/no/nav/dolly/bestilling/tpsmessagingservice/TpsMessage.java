package no.nav.dolly.bestilling.tpsmessagingservice;

import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@FunctionalInterface
public interface TpsMessage extends BiFunction<RsDollyUtvidetBestilling, DollyPerson,
        Map<String, List<TpsMeldingResponseDTO>>> {
}
