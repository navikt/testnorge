package no.nav.registre.tpsidentservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Set;

import no.nav.registre.tpsidentservice.consumer.TpsfConsumer;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentController {

    private final TpsfConsumer consumer;

    @GetMapping("/{ident}")
    public ResponseEntity<String> getIdent(@PathParam("ident") String ident) {
        var response = consumer.getIdent(ident);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ident}/miljoer")
    public ResponseEntity<Set<String>> getMiljoerForIdent(@PathParam("ident") String ident) {
        var response = consumer.getMiljoer(ident);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

}
