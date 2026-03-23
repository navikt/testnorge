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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/varslinger/person/ids")
@RequiredArgsConstructor
public class VarslingerPersonController {

    private final PersonVarslingAdapter personVarslingAdapter;

    @GetMapping
    public List<String> getVarslingerIds() {
        return personVarslingAdapter.getAll();
    }

    // TODO: Vi sjekker ikke om varslingId eksisterer; kan fort ryke på en NPE i PersonVarslingAdapter#save hvis så er tilfelle.
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
    public String getPersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        return Optional
                .ofNullable(personVarslingAdapter.get(varslingId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{varslingId}")
    public void deletePersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        personVarslingAdapter.delete(varslingId);
    }

}
