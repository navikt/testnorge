package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientRegister;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/status", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatusController {
    private final List<ClientRegister> clientRegisters;

    @GetMapping("")
    @Operation(description = "Hent status for Dolly forbrukere")
    public Object clientsStatus() {
        return clientRegisters.stream()
                .map(client -> Arrays.asList(client.getClass().getSimpleName(), client.status()))
                .collect(Collectors.toMap(key -> key.get(0), value -> value.get(1)));
    }
}
