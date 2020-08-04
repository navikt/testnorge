package no.nav.registre.testnorge.hendelse.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.dto.hendelse.v1.HendelseType;
import no.nav.registre.testnorge.hendelse.adapter.HendelseAdapter;
import no.nav.registre.testnorge.hendelse.domain.Hendelse;

@RestController
@RequestMapping("/api/v1/hendelser")
@RequiredArgsConstructor
public class HendelseController {

    private final HendelseAdapter adapter;

    @GetMapping
    public ResponseEntity<List<HendelseDTO>> hentHendelser(
            @RequestParam(value= "type", required = false) HendelseType type,
            @RequestParam(value = "between", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate between,
            @RequestParam(required = false) String ident
    ) {
        List<Hendelse> hendelses = adapter.hentHendelser(type, ident, between);
        return ResponseEntity.ok(hendelses.stream().map(Hendelse::toDTO).collect(Collectors.toList()));
    }
}
