package no.nav.testnav.apps.importfratpsfservice.provider.v1;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.importfratpsfservice.service.TpsfService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentController {

    private final TpsfService tpsfService;

    @Operation(description = "Leser SKD-meldinger fra TPSF-gruppe og legger disse inn PDL-forvalter + eksisterende Dolly-gruppe")
    @PutMapping("/gruppe/{gruppeId}")
    public List<TpsIdentStatusDTO> importIdenter(@PathVariable Long skdgruppeId,
                                                       @RequestParam Long dollyGruppeId) {

        return tpsfService.importIdenter(skdgruppeId, dollyGruppeId);

    }
}
