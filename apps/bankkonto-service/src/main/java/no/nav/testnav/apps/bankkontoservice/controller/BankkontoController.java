package no.nav.testnav.apps.bankkontoservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.bankkontoservice.dto.TpsMeldingResponse;
import no.nav.testnav.apps.bankkontoservice.service.BankkontoNorskService;
import no.nav.testnav.apps.bankkontoservice.service.BankkontoUtlandService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsMeldingResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class BankkontoController {

    private final BankkontoUtlandService bankkontoUtlandService;
    private final BankkontoNorskService bankkontoNorskService;

    @PostMapping("/{ident}/bankkonto-norsk")
    public List<TpsMeldingResponseDTO> endreNorskBankkonto(@PathVariable String ident,
                                                           @RequestBody BankkontonrNorskDTO bankkontonrNorsk,
                                                           @RequestParam(required = false) List<String> miljoer) {

        return convert(bankkontoNorskService.sendBankkontonrNorsk(ident, bankkontonrNorsk, miljoer));
    }

    @PostMapping("/{ident}/bankkonto-utenlandsk")
    public List<TpsMeldingResponseDTO> endreUtenlandskBankkonto(@PathVariable String ident,
                                                                @RequestBody BankkontonrUtlandDTO bankkontonrUtland,
                                                                @RequestParam(required = false) List<String> miljoer) {

        return convert(bankkontoUtlandService.sendBankkontonrUtland(ident, bankkontonrUtland, miljoer));
    }

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
}
