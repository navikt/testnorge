package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.BestillingProgress;

import java.util.function.Supplier;

@FunctionalInterface
public interface ClientFuture extends Supplier<BestillingProgress> {
}
