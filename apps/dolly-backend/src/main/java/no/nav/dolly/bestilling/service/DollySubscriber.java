package no.nav.dolly.bestilling.service;

import no.nav.dolly.domain.jpa.BestillingProgress;
import reactor.core.publisher.BaseSubscriber;

public class DollySubscriber extends BaseSubscriber<BestillingProgress> {

    @Override
    protected void hookOnComplete() {
        request(1);
    }

}
