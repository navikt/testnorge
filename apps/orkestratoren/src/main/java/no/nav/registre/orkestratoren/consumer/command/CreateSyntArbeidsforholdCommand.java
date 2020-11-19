package no.nav.registre.orkestratoren.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.synt.arbeidsforhold.v1.SyntArbeidsforholdDTO;

@Slf4j
@DependencyOn("testnorge-synt-arbeidsforhold-api")
@RequiredArgsConstructor
public class CreateSyntArbeidsforholdCommand implements Callable<Void> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;
    private final LocalDate foedselsdato;

    @Override
    public Void call() {
        var request = RequestEntity
                .post(URI.create(this.url + "/api/v1/synt-arbeidsforhold"))
                .body(new SyntArbeidsforholdDTO(foedselsdato, ident));
        var response = restTemplate.exchange(request, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Klarer ikke å opprette arbeidsforhold for " + ident);
        } else {
            log.info("Arbeidsforhold opprettet for {}", ident);
        }
        return response.getBody();
    }
}
