package no.nav.registre.testnorge.jenkinsbatchstatusservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.retry.RetryCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.retry.RetryConfig;

@Service
@RequiredArgsConstructor
public class RetryService {
    private final Executor executor;

    public void execute(RetryConfig config, Runnable runnable) {
        var command = new RetryCommand(runnable, config);
        this.executor.execute(command);
    }
}
