package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.consumer.OrgNameConsumer;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BestillingService {

    private final OrgNameConsumer orgNameConsumer;
    public BestillingResponse execute(BestillingRequest request) {

        String orgname = orgNameConsumer.getOrgName();
        return null;
    }
}
