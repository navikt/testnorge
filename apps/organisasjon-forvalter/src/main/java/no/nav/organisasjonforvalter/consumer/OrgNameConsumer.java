package no.nav.organisasjonforvalter.consumer;

import org.springframework.stereotype.Service;

@Service
public class OrgNameConsumer {

    private static final String NAME_URL = "/api/v1/navn";

//    private final WebClient webClient;

//    public OrgNameConsumer(
//            @Value("${orgNameUrl}") String baseUrl) {
//
//        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
//    }

    public String getOrgName() {

//        webClient.get()
        return null;
    }
}
