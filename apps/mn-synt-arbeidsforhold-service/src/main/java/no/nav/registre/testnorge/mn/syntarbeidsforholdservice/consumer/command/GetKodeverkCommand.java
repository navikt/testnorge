package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.core.headers.NavHeaders;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.dto.KodeverkDTO;

@Slf4j
@DependencyOn("kodeverk")
@RequiredArgsConstructor
public class GetKodeverkCommand implements Callable<KodeverkDTO> {
    private final WebClient webClient;
    private final String kodeverksnavn;
    private final LocalDate oppslagsdato;
    private final String applicationName;

    @Override
    public KodeverkDTO call() {
        log.info("Henter kodeverk med navn {}.", kodeverksnavn);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/kodeverk/{kodeverksnavn}/koder/betydninger")
                            .queryParam("ekskluderUgyldige", true)
                            .queryParam("oppslagsdato", format(oppslagsdato))
                            .build(kodeverksnavn)
                    )
                    .header(NavHeaders.NAV_CALL_ID, "Dolly")
                    .header(NavHeaders.NAV_CONSUMER_ID, applicationName)
                    .retrieve()
                    .bodyToMono(KodeverkDTO.class)
                    .block();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    private String format(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
