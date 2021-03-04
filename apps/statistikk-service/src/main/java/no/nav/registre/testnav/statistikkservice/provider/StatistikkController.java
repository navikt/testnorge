package no.nav.registre.testnav.statistikkservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkDTO;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;
import no.nav.registre.testnav.statistikkservice.adapter.StatistikkAdapter;
import no.nav.registre.testnav.statistikkservice.domain.Statistikk;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/statistikk")
public class StatistikkController {
    private final StatistikkAdapter adapter;

    @GetMapping
    public ResponseEntity<List<StatistikkDTO>> findAll() {
        List<StatistikkDTO> list = adapter.findAll().stream().map(Statistikk::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping
    public ResponseEntity<StatistikkDTO> save(@RequestBody StatistikkDTO dto) {
        Statistikk statistikk = adapter.save(new Statistikk(dto));
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{type}")
                .buildAndExpand(statistikk.getType())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{type}")
    public ResponseEntity<StatistikkDTO> find(@PathVariable("type") StatistikkType type) {
        Statistikk statistikk = adapter.find(type);
        if (statistikk == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(statistikk.toDTO());
    }
}
