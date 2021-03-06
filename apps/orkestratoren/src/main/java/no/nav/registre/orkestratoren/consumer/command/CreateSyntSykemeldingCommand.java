package no.nav.registre.orkestratoren.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;

@Slf4j
@RequiredArgsConstructor
public class CreateSyntSykemeldingCommand implements Callable<Void> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;
    private final ArbeidsforholdDTO arbeidsforhold;
    private final LocalDate startDato;


    @Override
    public Void call() {
        var request = RequestEntity
                .post(URI.create(url + "/api/v1/synt-sykemelding"))
                .body(SyntSykemeldingDTO
                        .builder()
                        .ident(ident)
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .orgnummer(arbeidsforhold.getOrgnummer())
                        .startDato(startDato)
                        .build()
                );
        var response = restTemplate.exchange(request, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Klarte ikke a opprette arbeidsforhold for " + ident);
        }
        return response.getBody();
    }
}
