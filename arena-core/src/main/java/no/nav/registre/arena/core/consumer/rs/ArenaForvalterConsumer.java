package no.nav.registre.arena.core.consumer.rs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Component
@Slf4j
public class ArenaForvalterConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate postBrukere;

    public ArenaForvalterConsumer(@Value("${arena-forvalteren.rest-api.url}") String arenaForvalterServerUrl) {

    }

}
