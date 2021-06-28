package no.nav.registre.aareg.provider.rs.v2;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.consumer.rs.ArbeidsforholdServiceConsumer;
import no.nav.registre.aareg.domain.Arbeidsforhold;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("api/v2/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdControllerV2 {

    private final ArbeidsforholdServiceConsumer arbeidsforholdServiceConsumer;

    @GetMapping
    @ApiOperation(value = "Hent arbeidsforhold fra aareg.")
    public ResponseEntity<List<Arbeidsforhold>> hentArbeidsforhold(
            @RequestParam String ident,
            @RequestParam String miljoe
    ) {
        return ResponseEntity.ok(arbeidsforholdServiceConsumer.hentArbeidsforhold(ident, miljoe));
    }
}
