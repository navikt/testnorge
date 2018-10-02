package no.nav.identpool.ident.ajourhold.mq;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mq")
public class QueueContextController {

    @GetMapping(value = "env", produces = "text/plain")
    public ResponseEntity<String> getQueueEnvironContext() {
        String builder = "Included: "
                + QueueContext.getIncluded().toString()
                + "\r\n\r\nExcluded: "
                + QueueContext.getExcluded().toString();
        return ResponseEntity.ok(builder);
    }
}