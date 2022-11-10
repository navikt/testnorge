package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientRegister;
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
public class StatusController {
    private final List<ClientRegister> clientRegisters;

    private static final Map<String, String> clientNavnMapping = new HashMap<>();
    static {
        clientNavnMapping.put("DokarkivClient", "Dokumentarkiv (JOARK)");
        clientNavnMapping.put("KrrstubClient", "Digital kontaktinformasjon (DKIF)");
        clientNavnMapping.put("InstdataClient", "Instdata");
        clientNavnMapping.put("InntektsmeldingClient", "Inntektsmelding (ALTINN/JOARK)");
        clientNavnMapping.put("PersonServiceClient", "PersonService");
        clientNavnMapping.put("PdlForvalterClient", "Persondataløsningen (PDL)");
        clientNavnMapping.put("InntektstubClient", "Inntektstub");
        clientNavnMapping.put("UdiStubClient", "Utlendingsdirektoratet (UDI)");
        clientNavnMapping.put("SkjermingsRegisterClient", "Skjermingsregisteret");
        clientNavnMapping.put("PensjonforvalterClient", "Pensjon");
        clientNavnMapping.put("AaregClient", "Arbeidsregister (AAREG)");
        clientNavnMapping.put("KontoregisterClient", "Bankkontoregister");
        clientNavnMapping.put("SigrunStubClient", "Skatteinntekt grunnlag (SIGRUN)");
        clientNavnMapping.put("BrregstubClient", "Brønnøysundregistrene (BRREGSTUB)");
        clientNavnMapping.put("TagsHendelseslagerClient", "TagsHendelseslager");
        clientNavnMapping.put("SykemeldingClient", "Testnorge Sykemelding");
        clientNavnMapping.put("TpsMessagingClient", "TpsMessaging");
        clientNavnMapping.put("ArenaForvalterClient", "Arena fagsystem");
    }

    private static String getClientNavn(String classNavn) {
        if (clientNavnMapping.containsKey(classNavn)) {
            return clientNavnMapping.get(classNavn);
        }
        return classNavn;
    }

    @GetMapping("")
    @Operation(description = "Hent status for Dolly forbrukere")
    public Object clientsStatus() {
        var filterClients = Arrays.asList("PdlDataClient", "TagsHendelseslagerClient");

        return clientRegisters.parallelStream()
                .filter(client -> !filterClients.contains(client.getClass().getSimpleName()))
                .map(client -> Arrays.asList(getClientNavn(client.getClass().getSimpleName()), client.status()))
                .collect(Collectors.toMap(key -> key.get(0), value -> value.get(1)));
    }
}
