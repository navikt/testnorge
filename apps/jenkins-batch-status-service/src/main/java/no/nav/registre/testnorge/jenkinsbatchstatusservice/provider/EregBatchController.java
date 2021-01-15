package no.nav.registre.testnorge.jenkinsbatchstatusservice.provider;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.jenkinsbatchstatusservice.service.BatchService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ereg/batch/queue")
public class EregBatchController {
    private final BatchService batchService;

    @PostMapping("/items/{id}")
    public ResponseEntity<HttpStatus> register(
            @RequestHeader("miljo") String miljo,
            @RequestHeader("uuid") String uuid,
            @PathVariable("id") Long id
    ) {
        batchService.registerEregBestilling(uuid, miljo, id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
