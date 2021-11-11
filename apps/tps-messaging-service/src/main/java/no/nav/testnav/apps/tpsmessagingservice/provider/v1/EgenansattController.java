package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.OpphoerEgenansattService;
import no.nav.testnav.apps.tpsmessagingservice.service.OpprettEgenansattService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.EndringsmeldingResponseDTO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/egenansatt")
@RequiredArgsConstructor
public class EgenansattController {

    private final OpprettEgenansattService opprettEgenansattService;
    private final OpphoerEgenansattService opphoerEgenansattService;

    @PostMapping("/{ident}")
    public Map<String, EndringsmeldingResponseDTO> opprettEgenansatt(@PathVariable String ident,
                                                                     @RequestParam LocalDate fraOgMed,
                                                                     @RequestParam List<String> miljoer) {

        return opprettEgenansattService.opprettEgenansatt(ident, fraOgMed, miljoer);
    }

    @DeleteMapping("/{ident}")
    public Map<String, EndringsmeldingResponseDTO> opphoerEgenansatt(@PathVariable String ident,
                                                                     @RequestParam List<String> miljoer) {

        return opphoerEgenansattService.opphoerEgenansatt(ident, miljoer);
    }
}
