package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.KontaktopplysningerService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kontaktopplysninger")
@RequiredArgsConstructor
public class KontaktopplysningerController {

    private final KontaktopplysningerService kontaktopplysningerService;

    @PostMapping("/{ident}")
    public List<TpsMeldingResponseDTO> sendKontaktopplysninger(@PathVariable String ident,
                                                               @RequestParam List<String> miljoer,
                                                               @RequestBody KontaktopplysningerRequestDTO kontaktopplysninger) {

        var kontaktopplysningerResultat = kontaktopplysningerService.sendKontaktopplysninger(ident, kontaktopplysninger, miljoer);
        return kontaktopplysningerResultat.entrySet().stream()
                .map(entry -> TpsMeldingResponseDTO.builder()
                        .miljoe(entry.getKey())
                        .status(entry.getValue().getReturStatus())
                        .melding(entry.getValue().getReturMelding())
                        .utfyllendeMelding(entry.getValue().getUtfyllendeMelding())
                        .build())
                .toList();
    }
}
