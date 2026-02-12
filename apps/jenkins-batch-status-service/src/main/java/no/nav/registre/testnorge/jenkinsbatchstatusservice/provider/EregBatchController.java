package no.nav.registre.testnorge.jenkinsbatchstatusservice.provider;


import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.jenkinsbatchstatusservice.service.BatchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ereg/batch/queue")
public class EregBatchController {
    private final BatchService batchService;

    @PostMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> register(
            @RequestHeader("miljo") String miljo,
            @RequestHeader("uuid") String uuid,
            @PathVariable("id") Long id
    ) {
        batchService.registerEregBestilling(uuid, miljo, id)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
        return Mono.empty();
    }
}
