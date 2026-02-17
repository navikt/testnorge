package no.nav.registre.varslingerservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.registre.varslingerservice.adapter.PersonVarslingAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/varslinger/person/ids")
@RequiredArgsConstructor
public class VarslingerPersonController {

    private final PersonVarslingAdapter personVarslingAdapter;

    @GetMapping
    public Mono<List<String>> getVarslingerIds() {
        return personVarslingAdapter.getAll();
    }

    // TODO: Vi sjekker ikke om varslingId eksisterer; kan fort ryke på en NPE i PersonVarslingAdapter#save hvis så er tilfelle.
    @PutMapping("/{varslingId}")
    public Mono<ResponseEntity<HttpStatus>> updatePersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        return personVarslingAdapter.save(varslingId)
                .map(saved -> {
                    var uri = UriComponentsBuilder
                            .fromPath("/api/v1/varslinger/person/ids/{varslingId}")
                            .buildAndExpand(saved)
                            .toUri();
                    return ResponseEntity.created(uri).build();
                });
    }

    @GetMapping("/{varslingId}")
    public Mono<String> getPersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        return personVarslingAdapter.get(varslingId)
                .flatMap(opt -> opt
                        .map(Mono::just)
                        .orElseGet(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))));
    }

    @DeleteMapping("/{varslingId}")
    public Mono<Void> deletePersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        return personVarslingAdapter.delete(varslingId);
    }

}
