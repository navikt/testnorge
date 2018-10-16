package no.nav.identpool.ident.ajourhold.mq;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mq")
@ConditionalOnProperty(name = "generer.identer.enable", matchIfMissing = true)
public class QueueContextController {

    @GetMapping(value = "env", produces = "text/plain")
    public ResponseEntity<String> getQueueEnvironContext() {
        String builder = "Included: "
                + QueueContext.getIncluded()
                + "\r\n\r\nExcluded: "
                + QueueContext.getExcluded();
        return ResponseEntity.ok(builder);
    }
}