package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.repository.InformasjonsmeldingRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/infostripe", produces = MediaType.APPLICATION_JSON_VALUE)
public class InfostripeController {

    private InformasjonsmeldingRepository informasjonsmeldingRepository;

    @GetMapping()
    @Operation(description = "Hent alle gyldige informasjons meldinger")
    public Set<Object> hentAlle() {
        informasjonsmeldingRepository.findAll()
        return new HashSet<>();
    }

    @PostMapping()
    @Operation(description = "Oppret ny informasjons melding")
    public void oppretNyMelding() {

    }

    @PutMapping("{id}")
    @Operation(description = "Oppdater informasjons melding")
    public void oppdaterMeldig(@PathVariable("id") Long id) {

    }

    @DeleteMapping("{id}")
    @Operation(description = "Slett informasjons melding")
    public void slettMelding(@PathVariable("id") Long id) {

    }
}
