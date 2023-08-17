package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import no.nav.dolly.domain.resultset.NavStatus;
import no.nav.dolly.domain.resultset.SystemStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/status", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class StatusController {
    private static final Map<String, String> consumerNavnMapping = new HashMap<>();
    private static final List<String> excludeConsumers = List.of("PdlPersonConsumer");

    static {
        consumerNavnMapping.put("DokarkivConsumer", "Dokumentarkiv (JOARK)");
        consumerNavnMapping.put("MedlConsumer", "Medlemskap (MEDL)");
        consumerNavnMapping.put("KrrstubConsumer", "Digital kontaktinformasjon (DKIF)");
        consumerNavnMapping.put("InstdataConsumer", "Instdata");
        consumerNavnMapping.put("InntektsmeldingConsumer", "Inntektsmelding (ALTINN/JOARK)");
        consumerNavnMapping.put("PersonServiceConsumer", "PersonService");
        consumerNavnMapping.put("PdlForvalterConsumer", "Persondataløsningen (PDL)");
        consumerNavnMapping.put("InntektstubConsumer", "Inntektstub");
        consumerNavnMapping.put("UdiStubConsumer", "Utlendingsdirektoratet (UDI)");
        consumerNavnMapping.put("SkjermingsRegisterConsumer", "Skjermingsregisteret");
        consumerNavnMapping.put("PensjonforvalterConsumer", "Pensjon");
        consumerNavnMapping.put("AaregConsumer", "Arbeidsregister (AAREG)");
        consumerNavnMapping.put("KontoregisterConsumer", "Bankkontoregister");
        consumerNavnMapping.put("SigrunStubConsumer", "Skatteinntekt grunnlag (SIGRUN)");
        consumerNavnMapping.put("BrregstubConsumer", "Brønnøysundregistrene (BRREGSTUB)");
        consumerNavnMapping.put("TagsHendelseslagerConsumer", "TagsHendelseslager");
        consumerNavnMapping.put("SykemeldingConsumer", "Testnorge Sykemelding");
        consumerNavnMapping.put("TpsMessagingConsumer", "TpsMessaging");
        consumerNavnMapping.put("ArenaForvalterConsumer", "Arena fagsystem");
    }

    private final List<ConsumerStatus> consumerRegister;
    private final WebClient webClient;

    @GetMapping()
    @Operation(description = "Hent status for Dolly forbrukere")
    public Map<Object, Map<String, TestnavStatusResponse>> clientsStatus() {
        return consumerRegister
                .parallelStream()
                .filter(StatusController::isNotExcluded)
                .map(client -> List.of(getConsumerNavn(client.getClass().getSimpleName()), client.checkStatus(webClient)))
                .collect(Collectors.toMap(key -> key.get(0), value -> (Map<String, TestnavStatusResponse>) value.get(1)));
    }

    @GetMapping("/oppsummert")
    @Operation(description = "Hent oppsummert status for Dolly forbrukere")
    public NavStatus clientsStatusSummary() {
        var status = consumerRegister
                .parallelStream()
                .filter(StatusController::isNotExcluded)
                .map(client -> List.of(getConsumerNavn(client.getClass().getSimpleName()), client.checkStatus(webClient)))
                .collect(Collectors.toMap(key -> (String) key.get(0), value -> (Map<String, TestnavStatusResponse>) value.get(1)));

        status.values().forEach(temp -> {
            log.info(temp.toString());
            temp.values().forEach(dollyStatusResponse -> {
                log.info(dollyStatusResponse.toString());
            });
        });

        return NavStatus.builder()
                .status(status.values().stream()
                        .allMatch(statusResponseMap -> statusResponseMap.values().stream()
                                .allMatch(dollyStatusResponse -> dollyStatusResponse.getReady()
                                        .matches("OK"))) ? SystemStatus.OK : SystemStatus.ISSUE)
                .description(status.entrySet().stream()
                        .filter(entry -> !entry.getValue().values().stream().allMatch(dollyStatusResponse -> dollyStatusResponse.getReady().matches("OK")))
                        .map(Map.Entry::getKey).collect(Collectors.joining(", "))) //TODO: Legg til description og sjekke om linje over fungerer
                .logLink("")  //TODO: Legg til loglink
                .build();
    }

    public static boolean isNotExcluded(ConsumerStatus consumer) {
        var consumerNavn = consumer.getClass().getSimpleName().split("\\$\\$")[0];
        return !excludeConsumers.contains(consumerNavn);
    }

    private static String getConsumerNavn(String classNavn) {
        var consumerNavn = classNavn.split("\\$\\$")[0];
        if (consumerNavnMapping.containsKey(consumerNavn)) {
            return consumerNavnMapping.get(consumerNavn);
        }
        return consumerNavn.replace("Consumer", "");
    }

}
