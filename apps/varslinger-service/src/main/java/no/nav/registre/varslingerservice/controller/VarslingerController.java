package no.nav.registre.varslingerservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.registre.varslingerservice.adapter.VarslingerAdapter;
import no.nav.registre.varslingerservice.domain.Varsling;
import no.nav.testnav.libs.dto.varslingerapi.v1.VarslingDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/api/v1/varslinger")
@RequiredArgsConstructor
public class VarslingerController {

    private final VarslingerAdapter varslingerAdapter;

    @GetMapping
    public Mono<List<VarslingDTO>> getVarslinger() {
        return Mono.fromCallable(() -> varslingerAdapter
                        .getAll()
                        .stream()
                        .map(Varsling::toDTO)
                        .toList())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping
    public Mono<ResponseEntity<Object>> oppdaterVarslinger(@RequestBody VarslingDTO dto) {
        return Mono.fromCallable(() -> varslingerAdapter.save(new Varsling(dto)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(varslingId -> {
                    var uri = UriComponentsBuilder.fromPath("/api/v1/varslinger")
                            .path("/{varslingId}")
                            .buildAndExpand(varslingId)
                            .toUri();
                    return ResponseEntity.created(uri).build();
                });
    }

    @DeleteMapping("/{varslingId}")
    public Mono<Void> deleteVarslinger(@PathVariable("varslingId") String varslingId) {
        // TODO: Returnerer alltid 200, selv om varslingId ikke finnes. Burde det returneres 404?
        return Mono.fromCallable(() -> {
                    varslingerAdapter.delete(varslingId);
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @GetMapping("/{varslingId}")
    public Mono<VarslingDTO> getVarslinger(@PathVariable("varslingId") String varslingId) {
        return Mono.defer(() -> Mono.justOrEmpty(varslingerAdapter.get(varslingId)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(Varsling::toDTO)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

}
