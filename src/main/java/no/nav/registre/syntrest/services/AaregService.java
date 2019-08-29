package no.nav.registre.syntrest.services;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.response.AaregResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class AaregService {
    @Value("${synth-aareg-app}")
    private String appName;
    @Value("${synth-aareg-url}")
    private String synthUrl;

    private final SyntConsumer<AaregResponse> syntConsumer;

    public AaregResponse generateData(String[] fnrs) {
        UriTemplate uri = new UriTemplate(synthUrl);
        RequestEntity request = RequestEntity.post(uri.expand()).body(fnrs);
        return syntConsumer.synthesizeData(appName, request);
    }

}
