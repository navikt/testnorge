package no.nav.registre.testnorge.personexportapi.consumer.command;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personexportapi.consumer.dto.GruppeDTO;

@RequiredArgsConstructor
public class GetTpsfGrupperCommand implements Callable<List<GruppeDTO>> {
    private final WebClient webClient;
    private final String username;
    private final String password;

    @Override
    public List<GruppeDTO> call() {
        GruppeDTO[] response = webClient
                .get()
                .uri("/api/v1/endringsmelding/skd/grupper")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                .retrieve()
                .bodyToMono(GruppeDTO[].class)
                .block();
        return Arrays.stream(response).collect(Collectors.toList());
    }
}
