package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.IdentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/identer")
public class IdentController {
    private final IdentService identService;

    @GetMapping
    public ResponseEntity<Set<String>> get(@RequestHeader("miljo") String miljo) {
        var identerMedArbeidsforhold = identService.getIdenterMedArbeidsforhold(miljo);
        return ResponseEntity
                .ok()
                .header("COUNT", String.valueOf(identerMedArbeidsforhold.size()))
                .body(identerMedArbeidsforhold);
    }
}
