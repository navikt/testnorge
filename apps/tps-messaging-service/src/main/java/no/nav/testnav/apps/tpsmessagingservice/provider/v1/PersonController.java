package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.apps.tpsmessagingservice.service.AdresseService;
import no.nav.testnav.apps.tpsmessagingservice.service.BankkontoNorskService;
import no.nav.testnav.apps.tpsmessagingservice.service.BankkontoUtlandService;
import no.nav.testnav.apps.tpsmessagingservice.service.EgenansattService;
import no.nav.testnav.apps.tpsmessagingservice.service.PersonService;
import no.nav.testnav.apps.tpsmessagingservice.service.SikkerhetstiltakService;
import no.nav.testnav.apps.tpsmessagingservice.service.SpraakService;
import no.nav.testnav.apps.tpsmessagingservice.service.TelefonnummerService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PostadresseUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final EgenansattService egenansattService;
    private final SpraakService spraakService;
    private final BankkontoUtlandService bankkontoUtlandService;
    private final BankkontoNorskService bankkontoNorskService;
    private final TelefonnummerService telefonnummerService;
    private final SikkerhetstiltakService sikkerhetstiltakService;
    private final AdresseService adresseService;

    private static List<TpsMeldingResponseDTO> convert(Map<String, TpsMeldingResponse> tpsMeldingDTO) {

        return tpsMeldingDTO.entrySet().stream()
                .map(entry -> TpsMeldingResponseDTO.builder()
                        .miljoe(entry.getKey())
                        .status(entry.getValue().getReturStatus())
                        .melding(entry.getValue().getReturMelding())
                        .utfyllendeMelding(entry.getValue().getUtfyllendeMelding())
                        .build())
                .toList();
    }

    @GetMapping("/{ident}")
    public List<PersonMiljoeDTO> getPerson(@PathVariable String ident,
                                           @RequestParam(required = false) List<String> miljoer) {

        return personService.getPerson(ident, nonNull(miljoer) ? miljoer : emptyList());
    }

    @PostMapping("/{ident}/egenansatt")
    public List<TpsMeldingResponseDTO> opprettEgenansatt(@PathVariable String ident,
                                                         @RequestParam
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                 LocalDate fraOgMed,
                                                         @RequestParam (required = false) List<String> miljoer) {

        return convert(egenansattService.opprettEgenansatt(ident, fraOgMed, miljoer));
    }

    @DeleteMapping("/{ident}/egenansatt")
    public List<TpsMeldingResponseDTO> opphoerEgenansatt(@PathVariable String ident,
                                                         @RequestParam (required = false) List<String> miljoer) {

        return convert(egenansattService.opphoerEgenansatt(ident, miljoer));
    }

    @PostMapping("/{ident}/spraakkode")
    public List<TpsMeldingResponseDTO> endreSpraakkode(@PathVariable String ident,
                                                       @RequestBody SpraakDTO spraak,
                                                       @RequestParam (required = false) List<String> miljoer) {

        return convert(spraakService.sendSpraakkode(ident, spraak, miljoer));
    }

    @PostMapping("/{ident}/bankkonto-norsk")
    public List<TpsMeldingResponseDTO> endreNorskBankkonto(@PathVariable String ident,
                                                           @RequestBody BankkontonrNorskDTO bankkontonrNorsk,
                                                           @RequestParam (required = false) List<String> miljoer) {

        return convert(bankkontoNorskService.sendBankkontonrNorsk(ident, bankkontonrNorsk, miljoer));
    }

    @PostMapping("/{ident}/bankkonto-utenlandsk")
    public List<TpsMeldingResponseDTO> endreUtenlandskBankkonto(@PathVariable String ident,
                                                                @RequestBody BankkontonrUtlandDTO bankkontonrUtland,
                                                                @RequestParam (required = false) List<String> miljoer) {

        return convert(bankkontoUtlandService.sendBankkontonrUtland(ident, bankkontonrUtland, miljoer));
    }

    @PostMapping("/{ident}/telefonnumre")
    public List<TpsMeldingResponseDTO> endreTelefonnummer(@PathVariable String ident,
                                                          @RequestBody List<TelefonnummerDTO> telefonnumre,
                                                          @RequestParam (required = false) List<String> miljoer) {

        return convert(telefonnummerService.endreTelefonnummer(ident, telefonnumre, miljoer));
    }

    @DeleteMapping("/{ident}/telefonnumre")
    public List<TpsMeldingResponseDTO> opphoerTelefonnummer(@PathVariable String ident,
                                                            @RequestParam List<TelefonnummerDTO.TypeTelefon> telefontyper,
                                                            @RequestParam (required = false) List<String> miljoer) {

        return convert(telefonnummerService.opphoerTelefonnummer(ident, telefontyper, miljoer));
    }

    @PostMapping("/{ident}/sikkerhetstiltak")
    public List<TpsMeldingResponseDTO> endreSikkerhetstiltak(@PathVariable String ident,
                                                             @RequestBody SikkerhetstiltakDTO sikkerhetstiltak,
                                                             @RequestParam (required = false) List<String> miljoer) {

        return convert(sikkerhetstiltakService.endreSikkerhetstiltak(ident, sikkerhetstiltak, miljoer));
    }

    @DeleteMapping("/{ident}/sikkerhetstiltak")
    public List<TpsMeldingResponseDTO> opphoerSikkerhetstiltak(@PathVariable String ident,
                                                             @RequestParam (required = false) List<String> miljoer) {

        return convert(sikkerhetstiltakService.opphoerSikkerhetstiltak(ident, miljoer));
    }

    @PostMapping("/{ident}/adresse")
    public List<TpsMeldingResponseDTO> endreAdresse(@PathVariable String ident,
                                                             @RequestBody PostadresseUtlandDTO postadresse,
                                                             @RequestParam (required = false) List<String> miljoer) {

        return convert(adresseService.endreAdresse(ident, postadresse, miljoer));
    }
}

