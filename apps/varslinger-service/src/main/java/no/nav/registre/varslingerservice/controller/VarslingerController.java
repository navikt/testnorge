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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/varslinger")
@RequiredArgsConstructor
public class VarslingerController {

    private final VarslingerAdapter varslingerAdapter;

    @GetMapping
    public List<VarslingDTO> getVarslinger() {
        return varslingerAdapter
                .getAll()
                .stream()
                .map(Varsling::toDTO)
                .toList();
    }

    @PutMapping
    public ResponseEntity<Object> oppdaterVarslinger(@RequestBody VarslingDTO dto) {
        var varslingId = varslingerAdapter.save(new Varsling(dto));
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{varslingId}")
                .buildAndExpand(varslingId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{varslingId}")
    public void deleteVarslinger(@PathVariable("varslingId") String varslingId) {
        // TODO: Returnerer alltid 200, selv om varslingId ikke finnes. Burde det returneres 404?
        varslingerAdapter.delete(varslingId);
    }

    @GetMapping("/{varslingId}")
    public VarslingDTO getVarslinger(@PathVariable("varslingId") String varslingId) {
        return Optional.ofNullable(varslingerAdapter.get(varslingId))
                .map(Varsling::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
