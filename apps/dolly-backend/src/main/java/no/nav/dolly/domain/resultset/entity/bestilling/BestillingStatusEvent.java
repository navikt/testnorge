package no.nav.dolly.domain.resultset.entity.bestilling;

import java.time.LocalDateTime;

public record BestillingStatusEvent(
        Long id,
        Long gruppeId,
        boolean ferdig,
        boolean stoppet,
        int antallLevert,
        int antallIdenter,
        String feil,
        LocalDateTime sistOppdatert
) {
}
