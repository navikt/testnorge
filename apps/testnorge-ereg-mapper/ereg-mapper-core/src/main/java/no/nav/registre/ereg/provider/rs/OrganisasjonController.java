package no.nav.registre.ereg.provider.rs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import no.nav.registre.ereg.domain.OrganisasjonList;
import no.nav.registre.ereg.provider.rs.request.EregDataRequest;
import no.nav.registre.ereg.service.FlatfileService;

@RestController
@RequestMapping("api/v1/organisasjoner")
public class OrganisasjonController {

    private final FlatfileService flatfileService;

    public OrganisasjonController(FlatfileService flatfileService) {
        this.flatfileService = flatfileService;
    }

    @PostMapping
    public ResponseEntity<List<EregDataRequest>> map(@RequestBody String flatfil) {
        OrganisasjonList organisasjonList = flatfileService.mapFlatfil(flatfil);
        return ResponseEntity.ok(organisasjonList.toEregDataRequestList());
    }
}
