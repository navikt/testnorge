package no.nav.registre.testnorge.avhengighetsanalyseservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.avhengighetsanalyseservice.adapter.ApplicationAdapter;
import no.nav.registre.testnorge.avhengighetsanalyseservice.domain.Application;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationAdapter adapter;

    @GetMapping
    public ResponseEntity<Set<String>> getApplications() {
        Set<String> applications = adapter
                .getAllApplications()
                .stream()
                .map(Application::getRegisteredName)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{name}")
    public ResponseEntity<HttpStatus> createApplications(@RequestParam("name") String name) {
        adapter.save(new Application(name));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<HttpStatus> deleteApplication(@RequestParam("name") String name) {
        adapter.deleteApplication(name);
        return ResponseEntity.noContent().build();
    }
}
