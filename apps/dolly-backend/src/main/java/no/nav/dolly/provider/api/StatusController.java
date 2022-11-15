package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ConsumerStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/status", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class StatusController {;
    private final List<ConsumerStatus> consumerRegister;

    private static final Map<String, String> consumerNavnMapping = new HashMap<>();
    static {
        consumerNavnMapping.put("DokarkivConsumer", "Dokumentarkiv (JOARK)");
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

    private static String getConsumerNavn(String classNavn) {
        var consumerNavn = classNavn.split("\\$\\$")[0];
        if (consumerNavnMapping.containsKey(consumerNavn)) {
            return consumerNavnMapping.get(consumerNavn);
        }
        return consumerNavn.replace("Consumer", "");
    }

    @GetMapping()
    @Operation(description = "Hent status for Dolly forbrukere")
    public Object clientsStatus() {
        return consumerRegister.parallelStream()
                .map(client -> Arrays.asList(getConsumerNavn(client.getClass().getSimpleName()), client.checkStatus()))
                .collect(Collectors.toMap(key -> key.get(0), value -> value.get(1)));
    }
}
