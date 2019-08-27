package no.nav.registre.syntrest.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesController;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;

@Slf4j
@RequiredArgsConstructor
public class SyntConsumer<T> {

    private final KubernetesController kubernetesController;
    private final String appName;
    private final String syntUrl;

    public SyntConsumer(KubernetesController kubernetesController, String appName) {
        this.appName = appName;
        this.kubernetesController = kubernetesController;
        // Få applikasjonsendepunkt fra kubernetesController.
        this.syntUrl = kubernetesController.getApiInterface(appName);
    }


}
