package no.nav.registre.hodejegeren.provider.rs;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;

@RestController
public class TriggeSyntetiseringController {
    
    @PostMapping("api/v1/syntetisering/generer")
    public List<String> genererSyntetiskeMeldingerOgLagreITpsf(@RequestBody GenereringsOrdreRequest ordreRequest) {
        return new ArrayList<>();
    }
}
