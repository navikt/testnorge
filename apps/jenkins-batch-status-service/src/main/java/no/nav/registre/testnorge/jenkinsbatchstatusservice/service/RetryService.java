package no.nav.registre.testnorge.jenkinsbatchstatusservice.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.retry.RetryCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.retry.RetryConfig;

@Service
public class RetryService {
    private final Executor executor;

    public RetryService() {
        this.executor = Executors.newFixedThreadPool(5);
    }

    public void execute(RetryConfig config, Runnable runnable) {
        var command = new RetryCommand(runnable, config);
        this.executor.execute(command);
    }
}
