package no.nav.identpool.batch.domain.mq;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mq")
public class QueueContextController {

    @GetMapping(value = "env", produces = "text/plain")
    public ResponseEntity<String> getQueueEnvironContext() {
        StringBuilder builder = new StringBuilder();
        builder.append("Included: ");
        QueueContext.getIncluded().stream().map(env -> env + ", ").forEach(builder::append);
        builder.append("\r\n\r\nExcluded: ");
        QueueContext.getExcluded().stream().map(env -> env + ", ").forEach(builder::append);
        return ResponseEntity.ok(builder.toString());
    }
}
