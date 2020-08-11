package no.nav.registre.testnorge.synt.arbeidsforhold.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

import no.nav.registere.testnorge.core.headers.NavHeaders;
import no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto.KodeverkDTO;

@RequiredArgsConstructor
public class GetKodeverkCommand implements Callable<KodeverkDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String kodeverksnavn;
    private final LocalDate oppslagsdato;
    private final String applicationName;


    @Override
    public KodeverkDTO call() {
        RequestEntity<Void> request = RequestEntity
                .get(URI.create(url + "/api/v1/kodeverk/" + kodeverksnavn
                        + "/koder/betydninger?ekskluderUgyldige=true&oppslagsdato=" + format(oppslagsdato))
                )
                .header(NavHeaders.NAV_CALL_ID, "Dolly")
                .header(NavHeaders.NAV_CONSUMER_ID, applicationName)
                .build();

        ResponseEntity<KodeverkDTO> response = restTemplate.exchange(request, KodeverkDTO.class);


        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            throw new RuntimeException("Klarer ikke å finne kodeverk " + kodeverksnavn);
        }
        return response.getBody();
    }

    private String format(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
