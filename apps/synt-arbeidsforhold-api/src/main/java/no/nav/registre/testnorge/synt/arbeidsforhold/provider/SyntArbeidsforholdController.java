package no.nav.registre.testnorge.synt.arbeidsforhold.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.dto.synt.arbeidsforhold.v1.SyntArbeidsforholdDTO;
import no.nav.registre.testnorge.synt.arbeidsforhold.service.SyntArbeidsforholdService;

@RestController
@RequestMapping("/api/v1/synt-arbeidsforhold")
@RequiredArgsConstructor
public class SyntArbeidsforholdController {

    private final SyntArbeidsforholdService service;

    @PostMapping
    public ResponseEntity<HttpStatus> generate(@RequestBody SyntArbeidsforholdDTO dto) {
        service.genrate(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
