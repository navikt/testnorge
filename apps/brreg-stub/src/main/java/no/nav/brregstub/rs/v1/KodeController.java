package no.nav.brregstub.rs.v1;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.common.RolleKode;
import no.nav.brregstub.api.common.UnderstatusKode;
import no.nav.brregstub.service.HentRolleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.EnumMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/kode")
@AllArgsConstructor
public class KodeController {

    private final HentRolleService service;

    @GetMapping("/roller")
    public Mono<EnumMap<RolleKode, String>> hentRollekoder() {
        var returValue = new EnumMap<RolleKode, String>(RolleKode.class);
        for (RolleKode rolleKode : RolleKode.values()) {
            returValue.put(rolleKode, rolleKode.getBeskrivelse());
        }

        return Mono.just(returValue);
    }

    @GetMapping("/understatus")
    public Mono<Map<Integer, String>> hentUnderstatuskoder() {
        return Mono.just(UnderstatusKode.understatusKoder);
    }

}
