package no.nav.registre.tp.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.tp.database.multitenancy.TenantContext;
import no.nav.registre.tp.provider.rs.request.OrkestreringRequest;
import no.nav.registre.tp.service.TpService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrkestreringController {

    private final TpService tpService;

    @PostMapping("/v1/orkestrering/init")
    public ResponseEntity initializeDatabase(@RequestBody OrkestreringRequest request) {
        TenantContext.setTenant(request.getMiljoe());

        int count = tpService.initializeTpDbForEnvironemnt(request.getAvspillergruppeId(), request.getMiljoe());

        return ResponseEntity.ok(count);
    }

}
