package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.ArbeidsfoholdService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/identer")
public class IdentController {

    private final ArbeidsfoholdService service;

    @PostMapping("/{ident}")
    public ResponseEntity<?> opprettArbeidsforholdForIdent(
            @RequestHeader("miljo") String miljo,
            @PathVariable("ident") String ident,
            @RequestParam("fom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fom,
            @RequestParam(value = "tom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tom
    ) {
        service.startArbeidsforhold(ident, fom, tom, miljo);
        return ResponseEntity.noContent().build();
    }
}
