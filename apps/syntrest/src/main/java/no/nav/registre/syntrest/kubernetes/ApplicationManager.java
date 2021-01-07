package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.SyntConsumer;

import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages lifecycle of the app. Abstracts away the call to the KubernetesController.
 * <p>
 * The application manager will only manage apps that is accessed through it, although the isAlive
 * method will check any application available on the NAIS cluster.
 * <p>
 * There is also an unsatisfying pattern where the scheduledExecutorService calls a shutdown signal
 * for an app, which the calls the shutdown for itself in the Application manager. This was done
 * because no arguments can be given to a callable function.
 */
@Slf4j
public class ApplicationManager {

    private final KubernetesController kubernetesController;
    private final ScheduledExecutorService scheduledExecutorService;

    private Map<String, ScheduledFuture<?>> activeApplications;

    public ApplicationManager(KubernetesController kubernetesController, ScheduledExecutorService scheduledExecutorService) {
        this.kubernetesController = kubernetesController;
        this.scheduledExecutorService = scheduledExecutorService;
        this.activeApplications = new ConcurrentHashMap<>();
    }

    public void startApplication(SyntConsumer app) throws ApiException, InterruptedException {
        if (!applicationIsAlive(app.getAppName())) {
            log.info("Starting synth package {}...", app.getAppName());
            kubernetesController.deployImage(app.getAppName());
        }

        if (activeApplications.containsKey(app.getAppName())) {
            activeApplications.get(app.getAppName()).cancel(false);
        }
    }

    public synchronized void scheduleShutdown(SyntConsumer app, long shutdownTimeDelaySeconds) {
        log.info("Scheduling shutdown for \'{}\' at {}.",
                app.getAppName(),
                DateUtils.addSeconds(new Date(), (int) shutdownTimeDelaySeconds));
        activeApplications.put(
                app.getAppName(),
                scheduledExecutorService.schedule(app::shutdownApplication, shutdownTimeDelaySeconds, TimeUnit.SECONDS));
    }

    public void shutdownApplication(String appId) {
        try {
            kubernetesController.takedownImage(appId);
            activeApplications.remove(appId);
        } catch (ApiException ignored) {
        }
    }

    public boolean applicationIsAlive(String appId) {
        return kubernetesController.isAlive(appId);
    }

    public Map<String, ScheduledFuture<?>> getActiveApplications() {
        return this.activeApplications;
    }
}
