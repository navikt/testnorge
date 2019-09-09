package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * Manages lifecycle of the app. Abstracts away the call to the KubernetesController,
 * and make sure the calls to start/end applications happens one at a time.
 * Is created as a Bean which means that only *one* synt package can be started/stopped at a time
 * on the NAIS cluster.
 * Does NOT ensure that only one call is given to a specific package at a time. This is done in the
 */
public class ApplicationManager {

    private final KubernetesController kubernetesController;

    public boolean applicationIsAlive(String appId) {
        return kubernetesController.isAlive(appId);
    }

    public synchronized void startApplication(String appId) {
        try {
            kubernetesController.deployImage(appId);
        } catch (ApiException | InterruptedException e) {
            log.error("Could not create application \'{}\'!", appId);
        }
    }

    public synchronized void shutdownApplication(String appId) {
        try {
            kubernetesController.takedownImage(appId);
        } catch (ApiException e) {
            log.error("Could not delete application \'{}\'.", appId);
        }
    }
}
