package no.nav.testnav.apps.instservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.instservice.consumer.InstTestdataConsumer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatusController {
    private final InstTestdataConsumer instTestdataConsumer;

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getStatus() {
        return instTestdataConsumer.checkStatus();
    }
}
