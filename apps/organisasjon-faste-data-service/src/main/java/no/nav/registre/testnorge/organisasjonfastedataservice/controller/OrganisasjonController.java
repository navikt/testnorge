package no.nav.registre.testnorge.organisasjonfastedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonService;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/organisasjoner")
public class OrganisasjonController {
    private final OrganisasjonService service;

    @GetMapping
    public ResponseEntity<List<OrganisasjonDTO>> getOrganisasjoner(
            @RequestParam(required = false) Gruppe gruppe,
            @RequestParam(required = false) Boolean kanHaArbeidsforhold,
            @RequestParam(required = false) String opprinnelse,
            @RequestParam(required = false) String tag
    ) {
        var organisasjoner = gruppe == null ? service.getOrganisasjoner() : service.getOrganisasjoner(gruppe);
        var list = organisasjoner
                .stream()
                .filter(value -> kanHaArbeidsforhold == null || value.kanHaArbeidsforhold() == kanHaArbeidsforhold)
                .filter(value -> tag == null || value.getTags().stream().map(String::toUpperCase).collect(Collectors.toSet()).contains(tag.toUpperCase()))
                .filter(value -> opprinnelse == null || value.getOpprinnelse() != null && value.getOpprinnelse().equals(opprinnelse))
                .map(Organisasjon::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping
    public Map<String, String> save(@RequestHeader Gruppe gruppe, @RequestBody List<OrganisasjonDTO> dtoListe) {

        Map<String, String> responseMap = new HashMap<>();

        dtoListe.forEach(dto -> {

            if (nonNull(dto.getOverenhet()) && service.getOrganisasjon(dto.getOverenhet()).isEmpty()) {
                handleError(responseMap, dto, "Kan ikke opprette organisasjon %s fordi overenhet %s ikke finnes i databasen.");
            } else if (isNull(dto.getForretningsAdresse()) && isNull(dto.getPostadresse())) {
                handleError(responseMap, dto, "Kan ikke opprette organisasjon %s med overenhet %s fordi den mangler begge typer adresse.");
            } else {
                service.save(new Organisasjon(dto), gruppe);

                URI uri = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{orgnummer}")
                        .buildAndExpand(dto.getOrgnummer())
                        .toUri();
                var response = ResponseEntity.created(uri).build();

                responseMap.put(dto.getOrgnummer(), HttpStatus.valueOf(response.getStatusCode().value()).name());
            }
        });
        return responseMap;
    }

    @GetMapping("/{orgnummer}")
    public ResponseEntity<OrganisasjonDTO> get(@PathVariable String orgnummer) {
        var organisasjon = service.getOrganisasjon(orgnummer);
        return organisasjon
                .map(value -> ResponseEntity.ok(value.toDTO()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{orgnummer}")
    public ResponseEntity<HttpStatus> delete(@PathVariable String orgnummer) {
        service.delete(orgnummer);
        return ResponseEntity.noContent().build();
    }

    private void handleError(Map<String, String> responseMap, OrganisasjonDTO dto, String errorMessage) {
        var error = String.format(
                errorMessage,
                dto.getOrgnummer(),
                dto.getOverenhet()
        );
        log.error(error);
        responseMap.put(dto.getOrgnummer(), error);
    }
}
