package no.nav.testnav.apps.syntaaregservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntaaregservice.consumer.request.RsAaregOpprettRequest;
import no.nav.testnav.apps.syntaaregservice.provider.response.RsAaregResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AaregService {

    private final AaregConsumer aaregWsConsumer;

    public RsAaregResponse opprettArbeidsforhold(
            RsAaregOpprettRequest request
    ) {
        return aaregWsConsumer.opprettArbeidsforhold(request);
    }

}
