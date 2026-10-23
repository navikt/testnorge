package no.nav.dolly.synt.tilleggsstonad.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;
import no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadService;
import no.nav.dolly.synt.tilleggsstonad.generator.TilleggsstonadType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Syntetisering")
@Slf4j
class TilleggsstonadController {

    private static final String TIL_DATO_BESKRIVELSE = "Bestemmer om innsendt tilDato verdi skal brukes eller om tilDato skal bli satt basert på syntetisk verdi. Default-verdi er satt til true.";

    private final TilleggsstonadService tilleggsstonadService;

    @PostMapping(value = "/boutgift", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_boutgift",
            summary = "Generer syntetiske boutgifter-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateBoutgift(@RequestBody List<VedtakRequestDto> requests,
                                                     @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.BOUTGIFT, requests, tilDato);
    }

    @PostMapping(value = "/boutgifter_arbeidssokere", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_boutgifter_arbeidssokere",
            summary = "Generer syntetiske boutgifter-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateBoutgifterArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                    @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.BOUTGIFTER_ARBEIDSSOKERE, requests, tilDato);
    }

    @PostMapping(value = "/daglig_reise", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_daglig_reise",
            summary = "Generer syntetiske daglig reise-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateDagligReise(@RequestBody List<VedtakRequestDto> requests,
                                                         @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.DAGLIG_REISE, requests, tilDato);
    }

    @PostMapping(value = "/daglig_reise_arbeidssoker", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_daglig_reise_arbeidssoker",
            summary = "Generer syntetiske daglig reise arbeidssoker-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateDagligReiseArbeidssoker(@RequestBody List<VedtakRequestDto> requests,
                                                                    @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.DAGLIG_REISE_ARBEIDSSOKER, requests, tilDato);
    }

    @PostMapping(value = "/laeremidler", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_laeremidler",
            summary = "Generer syntetiske laeremidler-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateLaeremidler(@RequestBody List<VedtakRequestDto> requests,
                                                         @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.LAEREMIDLER, requests, tilDato);
    }

    @PostMapping(value = "/laeremidler_arbeidssokere", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_laeremidler_arbeidssokere",
            summary = "Generer syntetiske laeremidler arbeidssokere-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateLaeremidlerArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                    @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.LAEREMIDLER_ARBEIDSSOKERE, requests, tilDato);
    }

    @PostMapping(value = "/flytting", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_flytting",
            summary = "Generer syntetiske flytting-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateFlytting(@RequestBody List<VedtakRequestDto> requests,
                                                      @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.FLYTTING, requests, tilDato);
    }

    @PostMapping(value = "/flytting_arbeidssokere", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_flytting_arbeidssokere",
            summary = "Generer syntetiske flytting arbeidssokere-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateFlyttingArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                  @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.FLYTTING_ARBEIDSSOKERE, requests, tilDato);
    }

    @PostMapping(value = "/reise_aktivitet_og_hjemreiser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_reise_akt",
            summary = "Generer syntetiske reise aktivitet og hjemreiser-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateReiseAktivitetOgHjemreiser(@RequestBody List<VedtakRequestDto> requests,
                                                                        @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.REISE_AKTIVITET_OG_HJEMREISER, requests, tilDato);
    }

    @PostMapping(value = "/reise_aktivitet_og_hjemreiser_arbeidssokere", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_reise_akt_arbeidssokere",
            summary = "Generer syntetiske reise aktivitet og hjemreiser arbeidssokere-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateReiseAktivitetOgHjemreiserArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                                    @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.REISE_AKTIVITET_OG_HJEMREISER_ARBEIDSSOKERE, requests, tilDato);
    }

    @PostMapping(value = "/reisestonad_til_arbeidssokere", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_reisestonad_til_arbeidssokere",
            summary = "Generer syntetiske reisestonad til arbeidssokere-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateReisestonadTilArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                         @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.REISESTONAD_TIL_ARBEIDSSOKERE, requests, tilDato);
    }

    @PostMapping(value = "/reise_til_obligatorisk_samling", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_reise_til_obligatorisk_samling",
            summary = "Generer syntetiske reise til obligatorisk samling-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateReiseTilObligatoriskSamling(@RequestBody List<VedtakRequestDto> requests,
                                                                         @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.REISE_TIL_OBLIGATORISK_SAMLING, requests, tilDato);
    }

    @PostMapping(value = "/reise_til_obligatorisk_samling_arbeidssokere", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_reise_til_obligatorisk_samling_arbeidssokere",
            summary = "Generer syntetiske reise til obligatorisk samling arbeidssokere-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateReiseTilObligatoriskSamlingArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                                      @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.REISE_TIL_OBLIGATORISK_SAMLING_ARBEIDSSOKERE, requests, tilDato);
    }

    @PostMapping(value = "/tilsyn_familiemedlemmer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_tilsyn_familiemedlemmer",
            summary = "Generer syntetiske tilsyn familiemedlemmer-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateTilsynFamiliemedlemmer(@RequestBody List<VedtakRequestDto> requests,
                                                                    @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.TILSYN_FAMILIEMEDLEMMER, requests, tilDato);
    }

    @PostMapping(value = "/tilsyn_familiemedlemmer_arbeidssokere", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_tilsyn_familiemedlemmer_arbeidssokere",
            summary = "Generer syntetiske tilsyn familiemedlemmer arbeidssokere-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateTilsynFamiliemedlemmerArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                                @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOKERE, requests, tilDato);
    }

    @PostMapping(value = "/tilsyn_barn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_tilsyn_barn",
            summary = "Generer syntetiske tilsyn barn-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateTilsynBarn(@RequestBody List<VedtakRequestDto> requests,
                                                        @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.TILSYN_BARN, requests, tilDato);
    }

    @PostMapping(value = "/tilsyn_barn_arbeidssoker", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_tilsyn_barn_arbeidssoker",
            summary = "Generer syntetiske tilsyn barn arbeidssoker-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "object")))
            )
    )
    Mono<List<Map<String, Object>>> generateTilsynBarnArbeidssoker(@RequestBody List<VedtakRequestDto> requests,
                                                                   @Parameter(description = TIL_DATO_BESKRIVELSE, schema = @Schema(type = "boolean")) @RequestParam(name = "tilDato", required = false) String tilDato) {
        return generateVedtak(TilleggsstonadType.TILSYN_BARN_ARBEIDSSOKER, requests, tilDato);
    }

    private Mono<List<Map<String, Object>>> generateVedtak(TilleggsstonadType type, List<VedtakRequestDto> requests, String tilDato) {
        return Mono.fromCallable(() -> {
                    var generated = tilleggsstonadService.generateVedtak(type, requests, brukInnsendtTilDato(tilDato));
                    log.info("Generated {} result(s) for {}", generated.size(), type);
                    return generated;
                })
                .subscribeOn(Schedulers.parallel());
    }

    private static boolean brukInnsendtTilDato(String tilDato) {

        if (nonNull(tilDato) && !"true".equalsIgnoreCase(tilDato) && !"false".equalsIgnoreCase(tilDato)) {
            throw new IllegalArgumentException("Query-parameteret tilDato må være true eller false, var '%s'".formatted(tilDato));
        }
        return !"false".equals(tilDato);

    }

}
