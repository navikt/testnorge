package no.nav.registre.testnorge.identservice.testdata.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Set;

public class IdentpoolConsumer {

    private static final String REKVIRERT_AV = "rekvirertAv";
    private static final String CONSUMER = "TPSF";
    private static final String IDENTPOOL_ROOT_URL = "/api/v1/identifikator";
    private static final String IDENT_WHITELIST_URL = IDENTPOOL_ROOT_URL + "/whitelist";
    private static final String IDENT_ACQUIRE_URL = IDENTPOOL_ROOT_URL + "/brukFlere" + '?' + REKVIRERT_AV + '='  + CONSUMER;
    private static final String IDENT_RELEASE_URL = IDENTPOOL_ROOT_URL + "/frigjoer"+ '?' + REKVIRERT_AV + '=' + CONSUMER;

    @Value("${identpool.host.url}")
    private String identpoolHost;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity getWhitelistedIdent() {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(identpoolHost + IDENT_WHITELIST_URL))
                .build(), String[].class);
    }

    public ResponseEntity requestSpecificIdents(List<String> idents) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(identpoolHost + IDENT_ACQUIRE_URL))
                .body(idents.toArray()), String[].class);
    }

    public ResponseEntity<String[]> recycleIdents(Set<String> request) {

        return restTemplate.exchange(RequestEntity.post(
                URI.create(identpoolHost + IDENT_RELEASE_URL))
                .body(request), String[].class);
    }
}
