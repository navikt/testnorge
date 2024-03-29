package no.nav.testnav.apps.organisasjonbestillingservice.retry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryCommand implements Runnable {
    private final Runnable runnable;
    private final long sleepMilliseconds;
    private final int totalAttempts;


    public RetryCommand(Runnable runnable, RetryConfig config) {
        log.info(
                "Retry satt opp med {} forsøk hvert {} millisecond.",
                config.getRetryAttempts(),
                config.getSleepMilliseconds()
        );
        this.runnable = runnable;
        this.sleepMilliseconds = config.getSleepMilliseconds();
        this.totalAttempts = config.getRetryAttempts();
    }

    @Override
    public void run() {
        for (int attempts = 1; attempts <= totalAttempts; attempts++) {
            try {
                runnable.run();
                log.info("Operasjonen ble utført etter {}/{} forsøk ({} ms).", attempts, totalAttempts, sleepMilliseconds * attempts);
                return;
            } catch (Exception e) {
                log.trace("Operasjonen ikke utført {} forsøk igjen.", totalAttempts - attempts, e);
            }
            try {
                log.trace("Venter i {} millisekunder...", sleepMilliseconds);
                Thread.sleep(sleepMilliseconds);
            } catch (InterruptedException e) {
                log.error("Feil ved tread sleep", e);
            }
        }
        log.error("Operasjonen ikke utført.");
    }
}