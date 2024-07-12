package no.nav.dolly;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DollyBackendLifecycle implements SmartLifecycle {

    private final BestillingRepository repository;

    private boolean isRunning = false;


    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void start() {
        isRunning = true;
        var result = repository.stopOrphanedRunning();
        log.info("Stopped {} orphans", result);
    }

    @Override
    public void stop() {
        isRunning = false;
    }

}
