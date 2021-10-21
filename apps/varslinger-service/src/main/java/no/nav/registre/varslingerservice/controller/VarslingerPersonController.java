package no.nav.registre.varslingerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

import no.nav.registre.varslingerservice.adapter.PersonVarslingAdapter;

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
    public ResponseEntity<?> getPersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        String id = personVarslingAdapter.get(varslingId);
        if (id == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{varslingId}")
    public ResponseEntity<?> deletePersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        personVarslingAdapter.delete(varslingId);
        return ResponseEntity.ok().build();
    }
}
