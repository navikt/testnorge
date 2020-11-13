package no.nav.registre.orkestratoren.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;

@Slf4j
@DependencyOn("testnorge-arbeidsforhold-api")
@RequiredArgsConstructor
public class GetArbeidsforholdCommand implements Callable<List<ArbeidsforholdDTO>> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;

    @Override
    public List<ArbeidsforholdDTO> call() {
        var requestEntity = RequestEntity
                .get(URI.create(url + "/api/v1/arbeidsforhold/" + ident))
                .build();

        var response = restTemplate.exchange(requestEntity, ArbeidsforholdDTO[].class);

        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            throw new RuntimeException("Klarer ikke å hente arbeidsforhold for " + ident);
        }
        return Arrays.stream(response.getBody()).collect(Collectors.toList());
    }
}