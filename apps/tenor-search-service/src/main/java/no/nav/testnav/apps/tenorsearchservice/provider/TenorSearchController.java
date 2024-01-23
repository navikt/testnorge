package no.nav.testnav.apps.tenorsearchservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.MaskinportenClient;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.InfoType;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.Kilde;
import no.nav.testnav.apps.tenorsearchservice.domain.AccessToken;
import no.nav.testnav.apps.tenorsearchservice.domain.Lookups;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import no.nav.testnav.apps.tenorsearchservice.service.LookupService;
import no.nav.testnav.apps.tenorsearchservice.service.TenorSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenor")
@RequiredArgsConstructor
public class TenorSearchController {

    private final TenorSearchService tenorSearchService;
    private final MaskinportenClient maskinportenClient;
    private final LookupService lookupService;

    @GetMapping("/testdata/raw")
    public Mono<TenorResponse> getTestdata(@RequestParam(required = false) String searchData,
                                           @RequestParam(required = false) Kilde kilde,
                                           @RequestParam(required = false) InfoType type,
                                           @RequestParam(required = false) String fields,
                                           @RequestParam(required = false) Integer seed) {

        return tenorSearchService
                .getTestdata(searchData, kilde, type, fields, seed);
    }

    @PostMapping("/testdata")
    public Mono<TenorResponse> getTestdata(@RequestBody TenorRequest searchData,
                                           @RequestParam(required = false) Kilde kilde,
                                           @RequestParam(required = false) InfoType type,
                                           @RequestParam(required = false) String fields,
                                           @RequestParam(required = false) Integer seed) {

        return tenorSearchService
                .getTestdata(searchData, kilde, type, fields, seed);
    }

    @GetMapping("/testdata/domain")
    public List<String> getTestdataDomain(@RequestParam Lookups lookup) {

        return lookupService.getLookup(lookup);
    }

    @GetMapping("/testdata/token")
    public Mono<String> getToken() {

        return maskinportenClient.getAccessToken()
                .map(AccessToken::value);
    }
}