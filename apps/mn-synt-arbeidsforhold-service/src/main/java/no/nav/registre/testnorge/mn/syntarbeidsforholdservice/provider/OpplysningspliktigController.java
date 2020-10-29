package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.ArbeidsfoholdService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/opplysningspliktig")
public class OpplysningspliktigController {

    private final ArbeidsfoholdService syntentiseringService;

    @PostMapping
    public ResponseEntity<?> generateForAll(
            @RequestHeader("kalendermaaned") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kalendermaaned
    ) {
        syntentiseringService.synt(kalendermaaned);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{orgnummer}")
    public ResponseEntity<?> generateFor(
            @PathVariable("orgnummer") String orgnummer,
            @RequestHeader("kalendermaaned") LocalDate kalendermaaned
    ) {
        return ResponseEntity.ok().build();
    }
}
