package no.nav.registre.testnorge.synt.sykemelding.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidsforholdCommand implements Callable<ArbeidsforholdDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;
    private final String orgnummer;
    private final String arbeidsforholdId;

    @Override
    public ArbeidsforholdDTO call() throws Exception {
        log.info("Henter arbeidsforhold for {} i org {} med id {}", ident, orgnummer, arbeidsforholdId);

        var requestEntity = RequestEntity
                .get(new URI(url + "/api/v1/arbeidsforhold/" + ident + "/" + orgnummer + "/" + arbeidsforholdId))
                .build();

        var response = restTemplate.exchange(requestEntity, ArbeidsforholdDTO.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error(
                    "Klarer ikke å hente arbeidsforhold i {} med id {} for {}. Response kode {}.",
                    arbeidsforholdId,
                    orgnummer,
                    ident,
                    response.getStatusCodeValue()
            );
        }
        return response.getBody();
    }
}
