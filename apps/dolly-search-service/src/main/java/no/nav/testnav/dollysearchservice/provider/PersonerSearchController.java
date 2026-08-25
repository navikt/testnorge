package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.dto.Kategori;
import no.nav.testnav.dollysearchservice.service.PersonerSearchService;
import no.nav.testnav.libs.dto.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchResponse;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import org.springframework.http.HttpCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@RestController
@RequestMapping("/api/v1/personer")
@RequiredArgsConstructor
public class PersonerSearchController {

    private final PersonerSearchService personerSearchService;
    private final GetUserInfo getUserInfo;

    @PostMapping
    @Operation(description = "Henter Dolly-personer som matcher både søk i registre og søk av persondetaljer i PDL")
    public Mono<SearchResponse> getPersoner(@RequestParam(required = false) List<ElasticTyper> registreRequest,
                                            @RequestBody SearchRequest request,
                                            @CookieValue MultiValueMap<String, HttpCookie> cookies) {

        cookies.forEach((key, value) -> log.info("Cookie: {} = {}", key, value));

        return getUserInfo.call()
                .doOnNext(userInfo -> log.info("Mottok søk, brukernavn: {}, brukerId: {}, isBankId: {}, " +
                                               "organisasjonsnummer: {}, issuer: {}, grupper: {}",
                        userInfo.brukernavn(), userInfo.id(), userInfo.isBankId(),
                        userInfo.organisasjonsnummer(), userInfo.issuer(), userInfo.grupper()))
                .map(userInfo -> {
                    if (isTrue(userInfo.isBankId())) {
                        request.setBrukerType("BANKID");
                        request.setOrgnr(userInfo.organisasjonsnummer());
                    } else {
                        request.setBrukerType("AZURE");
                    }
                    return personerSearchService.search(request, registreRequest);
                });
    }

    @GetMapping("/typer")
    @Operation(description = "Henter alle søketyper mot registre")
    public Mono<List<Kategori>> getKategorier() {

        return Mono.just(personerSearchService.getTyper());
    }
}
