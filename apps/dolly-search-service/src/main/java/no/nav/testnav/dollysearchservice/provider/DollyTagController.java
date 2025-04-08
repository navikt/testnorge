package no.nav.testnav.dollysearchservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.dto.TagsOpprettingResponse;
import no.nav.testnav.dollysearchservice.service.TagsService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class DollyTagController {

    private final TagsService tagsService;
    @PutMapping
    @Operation(description = "Oppdatere Testnorge-personer, set DOLLY-tag")
    public Mono<TagsOpprettingResponse> updateTestnorgeIdenter() {

        return tagsService.setDollyTagAlleTestnorgeIdenter();
    }
}
