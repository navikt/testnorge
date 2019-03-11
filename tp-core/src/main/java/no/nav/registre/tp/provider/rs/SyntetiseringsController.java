package no.nav.registre.tp.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import no.nav.registre.tp.provider.rs.request.SyntetiseringsRequest;
import no.nav.registre.tp.service.TpService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringsController {

    @Autowired
    private TpService tpService;

    @PostMapping(value = "/generer")
    public ResponseEntity createYtelseWithRelations(@RequestBody @Valid SyntetiseringsRequest request) {
        tpService.syntetiser(request);
        return ResponseEntity.ok().build();
    }

}
