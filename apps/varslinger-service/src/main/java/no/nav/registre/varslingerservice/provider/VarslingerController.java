package no.nav.registre.varslingerservice.provider;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.varslingerapi.v1.VarslingDTO;
import no.nav.registre.varslingerservice.adapter.PersonVarslingAdapter;
import no.nav.registre.varslingerservice.adapter.VarslingerAdapter;
import no.nav.registre.varslingerservice.domain.Varsling;

@RestController
@RequestMapping("/api/v1/varslinger")
@RequiredArgsConstructor
public class VarslingerController {

    private final VarslingerAdapter varslingerAdapter;
    private final PersonVarslingAdapter personVarslingAdapter;

    @GetMapping
    public ResponseEntity<List<VarslingDTO>> getVarslinger() {
        var list = varslingerAdapter.getAll().stream().map(Varsling::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping
    public ResponseEntity<?> oppdaterVarslinger(@RequestBody VarslingDTO dto) {
        String varslingId = varslingerAdapter.save(new Varsling(dto));
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{varslingId}")
                .buildAndExpand(varslingId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{varslingId}")
    public ResponseEntity<?> deleteVarslinger(@PathVariable("varslingId") String varslingId) {
        varslingerAdapter.delete(varslingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{varslingId}")
    public ResponseEntity<?> getVarslinger(@PathVariable("varslingId") String varslingId) {
        var varsling = varslingerAdapter.get(varslingId);
        if (varsling == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(varsling.toDTO());
    }

    @GetMapping("/person/ids")
    public ResponseEntity<List<String>> getVarslingerIds() {
        return ResponseEntity.ok(personVarslingAdapter.getAll());
    }

    @PutMapping("/person/ids/{varslingId}")
    public ResponseEntity<HttpStatus> updatePersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        String saved = personVarslingAdapter.save(varslingId);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(saved)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/person/ids/{varslingId}")
    public ResponseEntity<?> getPersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        String id = personVarslingAdapter.get(varslingId);
        if (id == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/person/ids/{varslingId}")
    public ResponseEntity<?> deletePersonVarslingerId(@PathVariable("varslingId") String varslingId) {
        personVarslingAdapter.delete(varslingId);
        return ResponseEntity.ok().build();
    }
}
