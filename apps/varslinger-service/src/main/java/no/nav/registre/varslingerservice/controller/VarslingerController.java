package no.nav.registre.varslingerservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.registre.varslingerservice.adapter.VarslingerAdapter;
import no.nav.registre.varslingerservice.domain.Varsling;
import no.nav.testnav.libs.dto.varslingerapi.v1.VarslingDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/varslinger")
@RequiredArgsConstructor
public class VarslingerController {

    private final VarslingerAdapter varslingerAdapter;

    @GetMapping
    public ResponseEntity<List<VarslingDTO>> getVarslinger() {
        var list = varslingerAdapter
                .getAll()
                .stream()
                .map(Varsling::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping
    public ResponseEntity<Object> oppdaterVarslinger(@RequestBody VarslingDTO dto) {
        String varslingId = varslingerAdapter.save(new Varsling(dto));
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{varslingId}")
                .buildAndExpand(varslingId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{varslingId}")
    public ResponseEntity<Object> deleteVarslinger(@PathVariable("varslingId") String varslingId) {
        varslingerAdapter.delete(varslingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{varslingId}")
    public ResponseEntity<VarslingDTO> getVarslinger(@PathVariable("varslingId") String varslingId) {
        return Optional
                .ofNullable(varslingerAdapter.get(varslingId))
                .map(varsling -> ResponseEntity.ok(varsling.toDTO()))
                .orElse(ResponseEntity.notFound().build());
    }

}
