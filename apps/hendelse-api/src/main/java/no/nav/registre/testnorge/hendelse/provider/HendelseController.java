package no.nav.registre.testnorge.hendelse.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.hendelse.adapter.HendelseAdapter;
import no.nav.registre.testnorge.hendelse.domain.Hendelse;

@RestController
@RequestMapping("/api/v1/hendelser")
@RequiredArgsConstructor
public class HendelseController {

    private final HendelseAdapter adapter;

    @GetMapping
    public ResponseEntity<List<HendelseDTO>> hentHendelser() {
        List<Hendelse> hendelses = adapter.hentHendelser();
        return ResponseEntity.ok(hendelses.stream().map(Hendelse::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{ident}")
    public ResponseEntity<List<HendelseDTO>> hentHendelse(@PathVariable("ident") String ident) {
        List<Hendelse> hendelses = adapter.hentHendelser(ident);
        return ResponseEntity.ok(hendelses.stream().map(Hendelse::toDTO).collect(Collectors.toList()));
    }
}
