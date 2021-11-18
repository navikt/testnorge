package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.EgenansattService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/egenansatt")
@RequiredArgsConstructor
public class EgenansattController {

    private final EgenansattService egenansattService;

    @PostMapping("/{ident}")
    public List<TpsMeldingResponseDTO> opprettEgenansatt(@PathVariable String ident,
                                                         @RequestParam
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                 LocalDate fraOgMed,
                                                         @RequestParam List<String> miljoer) {

        var egenansattStatus = egenansattService.opprettEgenansatt(ident, fraOgMed, miljoer);
        return egenansattStatus.entrySet().stream()
                .map(entry -> TpsMeldingResponseDTO.builder()
                        .miljoe(entry.getKey())
                        .status(entry.getValue().getReturStatus())
                        .melding(entry.getValue().getReturMelding())
                        .utfyllendeMelding(entry.getValue().getUtfyllendeMelding())
                        .build())
                .toList();
    }

    @DeleteMapping("/{ident}")
    public List<TpsMeldingResponseDTO> opphoerEgenansatt(@PathVariable String ident,
                                                                @RequestParam List<String> miljoer) {

        var egenansattStatus =  egenansattService.opphoerEgenansatt(ident, miljoer);
        return egenansattStatus.entrySet().stream()
                .map(entry -> TpsMeldingResponseDTO.builder()
                        .miljoe(entry.getKey())
                        .status(entry.getValue().getReturStatus())
                        .melding(entry.getValue().getReturMelding())
                        .utfyllendeMelding(entry.getValue().getUtfyllendeMelding())
                        .build())
                .toList();
    }
}
