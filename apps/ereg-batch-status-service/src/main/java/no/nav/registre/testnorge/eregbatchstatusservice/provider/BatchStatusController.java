package no.nav.registre.testnorge.eregbatchstatusservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.eregbatchstatusservice.consumer.EregConsumer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/batch/items")
public class BatchStatusController {
    private final EregConsumer eregConsumer;

    @GetMapping("/{id}")
    public ResponseEntity<Long> getStatusKode(@RequestHeader("miljoe") String miljo, @PathVariable("id") Long id) {
        var status = eregConsumer.getStatusKode(miljo, id);
        return ResponseEntity.ok(status);
    }
}
