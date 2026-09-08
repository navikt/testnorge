package no.nav.testnav.apps.tenorsearchservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenorMalMetrics {

    private final MeterRegistry meterRegistry;

    public void success(String operation) {
        increment(operation, "success");
    }

    public void failure(String operation) {
        increment(operation, "failure");
    }

    private void increment(String operation, String outcome) {
        meterRegistry.counter(
                "tenor_mal_operations_total",
                "operation", operation,
                "outcome", outcome).increment();
    }
}
