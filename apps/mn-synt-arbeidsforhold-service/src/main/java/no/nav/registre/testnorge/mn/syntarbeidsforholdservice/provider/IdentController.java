package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.ArbeidsforholdService;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.IdentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/identer")
public class IdentController {
    private final IdentService identService;

    @GetMapping
    public ResponseEntity<Set<String>> get(@RequestHeader("miljo") String miljo) {
        return ResponseEntity.ok(identService.getIdenterMedArbeidsforhold(miljo));
    }
}
