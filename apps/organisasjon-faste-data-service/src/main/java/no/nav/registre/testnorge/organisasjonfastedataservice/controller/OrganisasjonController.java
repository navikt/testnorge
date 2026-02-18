package no.nav.registre.testnorge.organisasjonfastedataservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.service.OrganisasjonService;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<List<OrganisasjonDTO>> getOrganisasjoner(
            @RequestParam(required = false) Gruppe gruppe,
            @RequestParam(required = false) Boolean kanHaArbeidsforhold,
            @RequestParam(required = false) String opprinnelse,
            @RequestParam(required = false) String tag
    ) {
        var orgMono = isNull(gruppe) ? service.getOrganisasjoner() : service.getOrganisasjoner(gruppe);
        return orgMono.map(organisasjoner -> organisasjoner
                .stream()
                .filter(value -> kanHaArbeidsforhold == null || value.kanHaArbeidsforhold() == kanHaArbeidsforhold)
                .filter(value -> tag == null || value.getTags().stream().map(String::toUpperCase).collect(Collectors.toSet()).contains(tag.toUpperCase()))
                .filter(value -> opprinnelse == null || value.getOpprinnelse() != null && value.getOpprinnelse().equals(opprinnelse))
                .map(Organisasjon::toDTO)
                .collect(Collectors.toList()));
    }

    @PutMapping
    public Mono<Map<String, String>> save(@RequestHeader Gruppe gruppe, @RequestBody List<OrganisasjonDTO> dtoListe) {

        Map<String, String> responseMap = new HashMap<>();

        return Flux.fromIterable(dtoListe)
                .flatMap(dto -> {
                    if (nonNull(dto.getOverenhet())) {
                        return service.getOrganisasjon(dto.getOverenhet())
                                .flatMap(opt -> {
                                    if (opt.isEmpty()) {
                                        handleError(responseMap, dto, "Kan ikke opprette organisasjon %s fordi overenhet %s ikke finnes i databasen.");
                                        return Mono.empty();
                                    }
                                    return saveOrg(responseMap, dto, gruppe);
                                });
                    }
                    return saveOrg(responseMap, dto, gruppe);
                })
                .then(Mono.just(responseMap));
    }

    private Mono<Void> saveOrg(Map<String, String> responseMap, OrganisasjonDTO dto, Gruppe gruppe) {
        if (isNull(dto.getForretningsAdresse()) && isNull(dto.getPostadresse())) {
            handleError(responseMap, dto, "Kan ikke opprette organisasjon %s med overenhet %s fordi den mangler begge typer adresse.");
            return Mono.empty();
        }
        return service.save(new Organisasjon(dto), gruppe)
                .doOnSuccess(v -> responseMap.put(dto.getOrgnummer(), HttpStatus.CREATED.name()));
    }

    @GetMapping("/{orgnummer}")
    public Mono<OrganisasjonDTO> get(@PathVariable String orgnummer) {
        return service.getOrganisasjon(orgnummer)
                .flatMap(opt -> opt
                        .map(value -> Mono.just(value.toDTO()))
                        .orElseGet(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))));
    }

    @DeleteMapping("/{orgnummer}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String orgnummer) {
        return service.delete(orgnummer);
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
