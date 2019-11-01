package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.utils.SyntAppNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
/**
 * Manages lifecycle of the app. Abstracts away the call to the KubernetesController,
 * and make sure the calls to start/end applications happens one at a time.
 * Is created as a Bean which means that only *one* synt package can be started/stopped at a time
 * on the NAIS cluster.
 * Does NOT ensure that only one call is given to a specific package at a time. This is done in the
 */
public class ApplicationManager {

    @Value("${synth-package-unused-uptime}")
    private long SHUTDOWN_TIME_DELAY_SECONDS;

    private final KubernetesController kubernetesController;
    private final ScheduledExecutorService scheduledExecutorService;
    private Map<String, ScheduledFuture<?>> activeApplications;

    public ApplicationManager(KubernetesController kubernetesController, ScheduledExecutorService scheduledExecutorService) {
        this.kubernetesController = kubernetesController;
        this.scheduledExecutorService = scheduledExecutorService;
        this.activeApplications = new HashMap<>();
    }

    public synchronized int startApplication(String appId) {
        int returnValue = -1;
        try {
            kubernetesController.deployImage(appId);
            returnValue = 0;
        } catch (ApiException | InterruptedException e) {
            log.error("Could not create application \'{}\'!", appId);
        }

        return returnValue;
    }

    public synchronized void updateAccessedPackages(SyntConsumer app) {
        if (activeApplications.containsKey(app.getAppName())) {

            log.info("Updating termination timer for \'{}\'", app.getAppName());
            activeApplications.get(app.getAppName()).cancel(true);
            activeApplications.put(app.getAppName(), scheduledExecutorService.schedule(app::shutdownApplication, SHUTDOWN_TIME_DELAY_SECONDS, TimeUnit.SECONDS));

        } else {

            log.info("Starting termination timer for \'{}\'", app.getAppName());
            activeApplications.put(app.getAppName(), scheduledExecutorService.schedule(app::shutdownApplication, SHUTDOWN_TIME_DELAY_SECONDS, TimeUnit.SECONDS));
        }
    }

    // If the application is in the activeApplications-list, this should return true without having to call
    // the isAlive for the kubernetesController. However, if it is done that way, we will not have control over
    // the applications started on the cluster in some other ways...
    public boolean applicationIsAlive(String appId) {
        return kubernetesController.isAlive(appId);
    }

    public synchronized void shutdownApplication(String appId) {
        try {
            kubernetesController.takedownImage(appId);
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("Could not delete application \'{}\'.\n{}", appId, e.getMessage());
        }
    }
}
