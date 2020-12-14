package no.nav.registre.aareg.consumer.ws;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@DependencyOn(value = "aareg-ws", external = true)
public class AaregBehandleArbeidsforhold {

    private static final String BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL = "https://modapp-$.adeo.no/aareg-services/BehandleArbeidsforholdService/v1";
    private static final List<String> validEnvironments = List.of("q0", "q1", "q2", "q3", "q4", "t0", "t1", "t2", "t3", "t4", "u2");

    public Map<String, String> fetchWsUrlsAllEnvironments() {

        Map<String, String> allEnvironmentUrls = new HashMap<>();

        validEnvironments.forEach(env -> {
            allEnvironmentUrls.put(env, BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL.replace("$", env));
        });

        return allEnvironmentUrls;
    }
}
