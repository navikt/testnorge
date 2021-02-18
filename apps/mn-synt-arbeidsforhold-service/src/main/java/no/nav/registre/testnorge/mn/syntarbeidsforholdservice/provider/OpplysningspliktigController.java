package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.ArbeidsforholdHistorikkService;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.ArbeidsforholdService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/opplysningspliktig")
public class OpplysningspliktigController {

    private final ArbeidsforholdService syntetiseringService;
    private final ArbeidsforholdHistorikkService arbeidsforholdHistorikkService;

    @PostMapping
    public ResponseEntity<?> generateForAll(
            @RequestHeader("miljo") String miljo,
            @RequestHeader("kalendermaaned") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kalendermaaned
    ) {
        syntetiseringService.reportAll(kalendermaaned, miljo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/historikk")
    public ResponseEntity<List<String>> generateHistorikk(
            @RequestHeader("miljo") String miljo,
            @RequestParam("maxIdenter") Integer maxIdenter,
            @RequestParam("fom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fom,
            @RequestParam("tom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tom
    ) {
        var identer = arbeidsforholdHistorikkService.reportAll(fom, tom, maxIdenter, miljo);

        if(identer.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(identer);
    }
}
