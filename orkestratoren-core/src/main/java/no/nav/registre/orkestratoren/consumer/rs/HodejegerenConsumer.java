package no.nav.registre.orkestratoren.consumer.rs;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HodejegerenConsumer {
    public List<Long> startSyntetisering(GenereringsOrdreRequest genereringsOrdreRequest) {
        return new ArrayList<>();
    }
}
