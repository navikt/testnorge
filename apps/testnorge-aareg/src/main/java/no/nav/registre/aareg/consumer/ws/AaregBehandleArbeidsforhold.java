package no.nav.registre.aareg.consumer.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.consumer.rs.MiljoerConsumer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AaregBehandleArbeidsforhold {

    private static final String BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL = "https://modapp-$.adeo.no/aareg-services/BehandleArbeidsforholdService/v1";
    private static final List<String> validEnvironments =
            List.of("q0", "q1", "q2", "q4", "q5", "q6", "t0", "t1", "t2", "t3", "t4", "t5", "t6");

    private final MiljoerConsumer miljoerConsumer;

    public Map<String, String> fetchWsUrlsAllEnvironments() {

        List<String> miljoeEnvironments;
        Map<String, String> allEnvironmentUrls = new HashMap<>();
        try {
            log.info("Henter miljøer fra miljoeConsumer..");
            miljoeEnvironments = miljoerConsumer.hentMiljoer().getEnvironments();
            log.info("Gjeldende miljøer i bruk er: " + miljoeEnvironments.toString());
        } catch (Exception e) {
            log.error("Klarte ikke å hente miljoer fra MiljoerConsumer, bruker statiske verdier", e);
            miljoeEnvironments = validEnvironments;
        }

        miljoeEnvironments.forEach(env -> allEnvironmentUrls.put(env, BEHANDLE_ARBEIDSFORHOLD_SERVICE_URL.replace("$", env)));

        return allEnvironmentUrls;
    }
}
