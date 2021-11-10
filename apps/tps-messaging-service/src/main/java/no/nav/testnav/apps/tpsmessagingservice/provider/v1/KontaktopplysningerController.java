package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.KontaktopplysningerService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.KontaktopplysningerRequestDTO;

@RestController
@RequestMapping("/api/v1/kontaktopplysninger")
@RequiredArgsConstructor
public class KontaktopplysningerController {

   private final KontaktopplysningerService kontaktopplysningerService;

    @PostMapping("/{ident}")
    public Map<String, String> sendKontaktopplysninger(@PathVariable String ident,
                                                       @RequestParam List<String> miljoer,
                                                       @RequestBody KontaktopplysningerRequestDTO kontaktopplysninger) {

        return kontaktopplysningerService.sendKontaktopplysninger(ident, kontaktopplysninger, miljoer);
    }
}
