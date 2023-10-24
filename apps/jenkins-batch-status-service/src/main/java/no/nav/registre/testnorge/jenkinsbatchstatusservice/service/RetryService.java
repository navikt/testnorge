package no.nav.registre.testnorge.jenkinsbatchstatusservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.retry.RetryCommand;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.retry.RetryConfig;
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
