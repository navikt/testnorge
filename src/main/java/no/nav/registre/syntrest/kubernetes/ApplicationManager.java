package no.nav.registre.syntrest.kubernetes;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
public class ApplicationManager {

    private final KubernetesController kubernetesController;
    // private final String manifestPath;

    public ApplicationManager(KubernetesController kubernetesController) {
        this.kubernetesController = kubernetesController;
        // this.manifestPath = "/nais/{}.yaml";
    }

    // only one thread is allowed to run at a time
    public synchronized void startApplication(String appId) {
        if (!kubernetesController.isAlive(appId)) {
            try {
                kubernetesController.deployImage(appId);
            } catch (ApiException | InterruptedException e) {
                log.error("Could not create application \'{}\'!", appId);
            }
        }
    }

    public synchronized void shutdownApplication(String appId) {
        // shut it down
        try {
            kubernetesController.takedownImage(appId);
        } catch (ApiException e) {
            log.error("Could not delete application \'{}\'.", appId);
        }
    }

}
