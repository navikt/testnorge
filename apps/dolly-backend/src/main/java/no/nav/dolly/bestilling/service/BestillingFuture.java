package no.nav.dolly.bestilling.service;

import no.nav.dolly.domain.jpa.BestillingProgress;

import java.util.function.Supplier;

@FunctionalInterface
public interface BestillingFuture extends Supplier<BestillingProgress> {
}
