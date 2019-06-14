package no.nav.registre.arena.consumer.rs;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

public class ArenaForvalterConsumer {

    private static final ParameterizedTypeReference<JSONObject> RESPONSE_TYPE = new ParameterizedTypeReference<JSONObject>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate hentNyeBrukereUrl;
}
