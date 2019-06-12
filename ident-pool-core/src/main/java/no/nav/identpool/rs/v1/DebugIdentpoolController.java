package no.nav.identpool.rs.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.service.IdentTpsService;

@RestController
@RequestMapping("/api/v1/debug")
public class DebugIdentpoolController {

    @Autowired
    private IdentTpsService identTpsService;

    @PostMapping
    public Set<TpsStatus> sjekkTpsStatus(@RequestBody List<String> identer) {
        return identTpsService.checkIdentsInTps(identer);
    }
}
