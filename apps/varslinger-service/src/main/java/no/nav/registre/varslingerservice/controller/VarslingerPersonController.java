package no.nav.registre.varslingerservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.registre.varslingerservice.adapter.PersonVarslingAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/varslinger/person/ids")
@RequiredArgsConstructor
public class VarslingerPersonController {

    private final PersonVarslingAdapter personVarslingAdapter;

    @GetMapping
    public ResponseEntity<List<String>> getVarslingerIds() {
        return ResponseEntity.ok(personVarslingAdapter.getAll());
    }

    @PutMapping("/{varslingId}")
    public ResponseEntity<HttpStatus> updatePersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        String saved = personVarslingAdapter.save(varslingId);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(saved)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{varslingId}")
    public ResponseEntity<String> getPersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        return Optional
                .ofNullable(personVarslingAdapter.get(varslingId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{varslingId}")
    public ResponseEntity<Object> deletePersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        personVarslingAdapter.delete(varslingId);
        return ResponseEntity.ok().build();
    }

}
