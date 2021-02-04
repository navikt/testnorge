package no.nav.registre.testnorge.synt.sykemelding.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetArbeidsforholdCommand implements Callable<ArbeidsforholdDTO> {
    private final WebClient webClient;
    private final String accessToken;
    private final String ident;
    private final String orgnummer;
    private final String arbeidsforholdId;

    @SneakyThrows
    @Override
    public ArbeidsforholdDTO call() {

        return webClient
                .get()
                .uri("/api/v1/arbeidsforhold/" + ident + "/" + orgnummer + "/" + arbeidsforholdId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(ArbeidsforholdDTO.class)
                .block();
    }
}
