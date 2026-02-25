package no.nav.testnav.apps.organisasjonbestillingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.organisasjonbestillingservice.retry.RetryCommand;
import no.nav.testnav.apps.organisasjonbestillingservice.retry.RetryConfig;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;


@Service
@RequiredArgsConstructor
public class RetryService {
    private final Executor executor;

    public void execute(RetryConfig config, Runnable runnable) {
        var command = new RetryCommand(runnable, config);
        this.executor.execute(command);
    }
}
