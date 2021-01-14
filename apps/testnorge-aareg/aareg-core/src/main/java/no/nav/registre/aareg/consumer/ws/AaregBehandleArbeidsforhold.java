package no.nav.registre.aareg.consumer.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.consumer.rs.MiljoerConsumer;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@DependencyOn(value = "aareg-ws", external = true)
public class AaregBehandleArbeidsforhold {

    private static final String BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL = "https://modapp-$.adeo.no/aareg-services/BehandleArbeidsforholdService/v1";

    private final MiljoerConsumer miljoerConsumer;

    public Map<String, String> fetchWsUrlsAllEnvironments() {

        Map<String, String> allEnvironmentUrls = new HashMap<>();
        List<String> validEnvironments = miljoerConsumer.hentMiljoer().getEnvironments();
        log.info("Gjeldende miljøer i bruk er: " + validEnvironments);

        validEnvironments.forEach(env -> allEnvironmentUrls.put(env, BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL.replace("$", env)));

        return allEnvironmentUrls;
    }
}
