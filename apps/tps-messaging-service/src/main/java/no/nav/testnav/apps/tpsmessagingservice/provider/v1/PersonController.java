package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.TpsMeldingResponse;
import no.nav.testnav.apps.tpsmessagingservice.service.AdressehistorikkService;
import no.nav.testnav.apps.tpsmessagingservice.service.BankkontoNorskService;
import no.nav.testnav.apps.tpsmessagingservice.service.BankkontoUtlandService;
import no.nav.testnav.apps.tpsmessagingservice.service.DoedsmeldingService;
import no.nav.testnav.apps.tpsmessagingservice.service.EgenansattService;
import no.nav.testnav.apps.tpsmessagingservice.service.FoedselsmeldingService;
import no.nav.testnav.apps.tpsmessagingservice.service.PersonService;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdressehistorikkRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonRequestDTO;
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
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final EgenansattService egenansattService;
    private final BankkontoUtlandService bankkontoUtlandService;
    private final BankkontoNorskService bankkontoNorskService;
    private final AdressehistorikkService adressehistorikkService;
    private final FoedselsmeldingService foedselsmeldingService;
    private final DoedsmeldingService doedsmeldingService;

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

    @Operation(description = "Hent persondata uten å eksponere ident")
    @PostMapping("/ident")
    public List<PersonMiljoeDTO> getPerson(@RequestBody PersonRequestDTO request,
                                           @RequestParam(required = false) List<String> miljoer) {

        return personService.getPerson(request.getIdent(), nonNull(miljoer) ? miljoer : emptyList());
    }

    @PostMapping("/{ident}/egenansatt")
    public List<TpsMeldingResponseDTO> opprettEgenansatt(@PathVariable String ident,
                                                         @RequestParam
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                         LocalDate fraOgMed,
                                                         @RequestParam(required = false) List<String> miljoer) {

        return convert(egenansattService.opprettEgenansatt(ident, fraOgMed, miljoer));
    }

    @DeleteMapping("/{ident}/egenansatt")
    public List<TpsMeldingResponseDTO> opphoerEgenansatt(@PathVariable String ident,
                                                         @RequestParam(required = false) List<String> miljoer) {

        return convert(egenansattService.opphoerEgenansatt(ident, miljoer));
    }

    @PostMapping("/{ident}/bankkonto-norsk")
    public List<TpsMeldingResponseDTO> endreNorskBankkonto(@PathVariable String ident,
                                                           @RequestBody BankkontonrNorskDTO bankkontonrNorsk,
                                                           @RequestParam(required = false) List<String> miljoer) {

        return convert(bankkontoNorskService.sendBankkontonrNorsk(ident, bankkontonrNorsk, miljoer));
    }

    @DeleteMapping("/{ident}/bankkonto-norsk")
    public List<TpsMeldingResponseDTO> opphoerNorskBankkonto(@PathVariable String ident,
                                                             @RequestParam(required = false) List<String> miljoer) {

        return convert(bankkontoNorskService.opphoerBankkontonrNorsk(ident, miljoer));
    }

    @PostMapping("/{ident}/bankkonto-utenlandsk")
    public List<TpsMeldingResponseDTO> endreUtenlandskBankkonto(@PathVariable String ident,
                                                                @RequestBody BankkontonrUtlandDTO bankkontonrUtland,
                                                                @RequestParam(required = false) List<String> miljoer) {

        return convert(bankkontoUtlandService.sendBankkontonrUtland(ident, bankkontonrUtland, miljoer));
    }

    @DeleteMapping("/{ident}/bankkonto-utenlandsk")
    public List<TpsMeldingResponseDTO> opphoerBankkontonrUtland(@PathVariable String ident,
                                                                @RequestParam(required = false) List<String> miljoer) {

        return convert(bankkontoUtlandService.opphoerBankkontonrUtland(ident, miljoer));
    }

    @PostMapping("/adressehistorikk")
    public List<AdressehistorikkDTO> personhistorikk(@RequestBody AdressehistorikkRequest request,
                                                     @RequestParam(required = false) List<String> miljoer) {

        return adressehistorikkService.hentHistorikk(request, isNull(miljoer) ? emptyList() : miljoer);
    }

    @PostMapping("/foedselsmelding")
    public FoedselsmeldingResponse sendFoedselsmelding(@RequestParam(required = false) List<String> miljoer,
                                                       @RequestBody FoedselsmeldingRequest persondata) {

        return foedselsmeldingService.sendFoedselsmelding(persondata, isNull(miljoer) ? emptyList() : miljoer);
    }

    @PostMapping("/doedsmelding")
    public DoedsmeldingResponse sendDoedsmelding(@RequestBody DoedsmeldingRequest request,
                                                 @RequestParam(required = false) List<String> miljoer) {

        return doedsmeldingService.sendDoedsmelding(request, isNull(miljoer) ? emptyList() : miljoer);
    }

    @DeleteMapping("/doedsmelding")
    public DoedsmeldingResponse annulerDoedsmelding(@RequestParam(required = false) List<String> miljoer,
                                                    @RequestBody PersonDTO persondata) {

        return doedsmeldingService.annulerDoedsmelding(persondata, isNull(miljoer) ? emptyList() : miljoer);
    }
}

