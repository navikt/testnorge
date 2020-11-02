package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.ArbeidsfoholdService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/identer")
public class IdentController {

    private final ArbeidsfoholdService service;

    @PostMapping("/{ident}")
    public ResponseEntity<?> opprettArbeidsforholdForIdent(
            @PathVariable("ident") String ident,
            @RequestHeader(value = "kalendermaaned", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kalendermaaned,
            @RequestHeader(value = "historikk", required = false) Boolean historikk
    ) {
        service.startArbeidsforhold(
                ident,
                kalendermaaned == null ? LocalDate.now().withDayOfMonth(1) : kalendermaaned,
                historikk != null && historikk
        );

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ident}")
                .buildAndExpand(ident)
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}
