package no.nav.registre.aareg.consumer.ws;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
@DependencyOn(value = "aareg-ws", external = true)
public class AaregBehandleArbeidsforhold {

    private static final String BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL = "https://modapp-$.adeo.no/aareg-core/BehandleArbeidsforholdService/v1";

    public Map<String, String> fetchWsUrlsAllEnvironments(String environment) {

        return  Collections.singletonMap(environment, BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL.replace("$", environment));
    }
}
