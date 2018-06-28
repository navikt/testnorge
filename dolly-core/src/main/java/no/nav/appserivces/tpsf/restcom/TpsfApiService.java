package no.nav.appserivces.tpsf.restcom;

import no.nav.appserivces.tpsf.domain.response.RsSkdMeldingResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TpsfApiService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String TPSF_BASE_URL = "api/v1/dolly/testdata";
    private static final String TPSF_OPPRETT_URL = "/personer";

    @Value("tpsf.server.url")
    private String tpsfServerUrl;

    public Object opprettPersoner(){
        String url = tpsfServerUrl + TPSF_BASE_URL + TPSF_OPPRETT_URL;

        Object request = null;

        ResponseEntity<RsSkdMeldingResponse> response = restTemplate.postForEntity(url, request, RsSkdMeldingResponse.class);

        if(response.getStatusCode() != HttpStatus.CREATED){
            //throw IllegalArgumentException;
        }

        return response.getBody();
    }
}
