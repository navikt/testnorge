package no.nav.registre.tp.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.tp.database.multitenancy.TenantContext;
import no.nav.registre.tp.provider.rs.request.OrkestreringRequest;
import no.nav.registre.tp.service.TpService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/orkestrering")
public class OrkestreringController {

    private final TpService tpService;

    @PostMapping("/init")
    public ResponseEntity<Integer> initializeDatabase(@RequestBody OrkestreringRequest request) {
        TenantContext.setTenant(request.getMiljoe());

        int count = tpService.initializeTpDbForEnvironemnt(request.getAvspillergruppeId(), request.getMiljoe());

        return ResponseEntity.ok(count);
    }

    @PostMapping("/opprettPersoner")
    public ResponseEntity<List<String>> addPeople(@RequestBody List<String> fnrs) {
        List<String> people = tpService.createPeople(fnrs);
        List<String> feilet = fnrs.parallelStream().filter(fnr -> !people.contains(fnr)).collect(Collectors.toList());
        return ResponseEntity.ok(feilet);
    }

}
