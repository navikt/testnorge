package no.nav.registre.orgnrservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
//@Component
public class EregConsumer {

    String miljoe;
    private WebClient webClient;

    EregConsumer (@Value("${ereg.api.url}") String url, WebClient webClient) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    public boolean checkOrgnummer (String orgnummer) {
        //Loope gjennom de aktuelle miljøene
        return true;
    }
    public boolean checkOrgnummer (String orgnummer, String miljoe) {
        return true;
    }
}
