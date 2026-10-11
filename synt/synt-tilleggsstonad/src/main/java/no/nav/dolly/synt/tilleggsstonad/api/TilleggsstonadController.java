package no.nav.dolly.synt.tilleggsstonad.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;
import no.nav.dolly.synt.tilleggsstonad.onnx.OnnxService;
import no.nav.dolly.synt.tilleggsstonad.onnx.TilleggsstonadType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Syntetisering")
@Slf4j
class TilleggsstonadController {

    private final OnnxService onnxService;

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
    List<Map<String, Object>> generateBoutgift(@RequestBody List<VedtakRequestDto> requests,
                                               @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.BOUTGIFT, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateBoutgifterArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                              @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.BOUTGIFTER_ARBEIDSSOKERE, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateDagligReise(@RequestBody List<VedtakRequestDto> requests,
                                                   @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.DAGLIG_REISE, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateDagligReiseArbeidssoker(@RequestBody List<VedtakRequestDto> requests,
                                                              @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.DAGLIG_REISE_ARBEIDSSOKER, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateLaeremidler(@RequestBody List<VedtakRequestDto> requests,
                                                   @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.LAEREMIDLER, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateLaeremidlerArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                              @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.LAEREMIDLER_ARBEIDSSOKERE, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateFlytting(@RequestBody List<VedtakRequestDto> requests,
                                                @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.FLYTTING, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateFlyttingArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                            @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.FLYTTING_ARBEIDSSOKERE, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateReiseAktivitetOgHjemreiser(@RequestBody List<VedtakRequestDto> requests,
                                                                  @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.REISE_AKTIVITET_OG_HJEMREISER, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateReiseAktivitetOgHjemreiserArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                              @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.REISE_AKTIVITET_OG_HJEMREISER_ARBEIDSSOKERE, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateReisestonadTilArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                   @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.REISESTONAD_TIL_ARBEIDSSOKERE, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateReiseTilObligatoriskSamling(@RequestBody List<VedtakRequestDto> requests,
                                                                   @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.REISE_TIL_OBLIGATORISK_SAMLING, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateReiseTilObligatoriskSamlingArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                                @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.REISE_TIL_OBLIGATORISK_SAMLING_ARBEIDSSOKERE, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateTilsynFamiliemedlemmer(@RequestBody List<VedtakRequestDto> requests,
                                                              @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.TILSYN_FAMILIEMEDLEMMER, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateTilsynFamiliemedlemmerArbeidssokere(@RequestBody List<VedtakRequestDto> requests,
                                                                          @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOKERE, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateTilsynBarn(@RequestBody List<VedtakRequestDto> requests,
                                                  @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.TILSYN_BARN, requests, brukInnsendtTilDato);
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
    List<Map<String, Object>> generateTilsynBarnArbeidssoker(@RequestBody List<VedtakRequestDto> requests,
                                                             @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {
        return generateVedtak(TilleggsstonadType.TILSYN_BARN_ARBEIDSSOKER, requests, brukInnsendtTilDato);
    }

    private List<Map<String, Object>> generateVedtak(TilleggsstonadType type, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        var generated = onnxService.generateVedtak(type, requests, brukInnsendtTilDato);
        log.info("Generated {} result(s) for {}", generated.size(), type);
        return generated;
    }

}
