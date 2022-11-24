package no.nav.registre.testnorge.generersyntameldingservice.provider;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import no.nav.registre.testnorge.generersyntameldingservice.provider.response.ArbeidsforholdDTO;
import no.nav.registre.testnorge.generersyntameldingservice.service.GenererService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/generer")
@RequiredArgsConstructor
public class SyntController {

    private final GenererService genererService;

    @GetMapping("/ordinaert")
    public List<ArbeidsforholdDTO> generateSyntheticAmeldingerOrdinaert(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdato,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sluttdato
    ) {
        return genererService.generateAmeldinger(startdato, sluttdato, ArbeidsforholdType.ordinaertArbeidsforhold);
    }

    @GetMapping("/maritimt")
    public List<ArbeidsforholdDTO> generateSyntheticAmeldingerMaritimt(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdato,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sluttdato
    ) {
        return genererService.generateAmeldinger(startdato, sluttdato, ArbeidsforholdType.maritimtArbeidsforhold);
    }

}
