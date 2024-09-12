package no.nav.dolly;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"dev", "local", "prod"})
@RequiredArgsConstructor
@Slf4j
public class DollyBackendLifecycle implements SmartLifecycle {

    private final BestillingRepository bestillingRepository;
    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;

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
    @Transactional
    public void start() {
        isRunning = true;
        var unfinishedBestilling = bestillingRepository.stopAllUnfinished();
        var unfinishedOrganisasjonBestilling = organisasjonBestillingRepository.stopAllUnfinished();
        log.info("Stoppet {} kjørende bestilling(er), {} kjørende organisasjonsbestilling(er)", unfinishedBestilling, unfinishedOrganisasjonBestilling);
    }

    @Override
    public void stop() {
        isRunning = false;
    }

}
