package no.nav.dolly.synt.aap.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.aap.dto.AapVedtakDto;
import no.nav.dolly.synt.aap.dto.AatforAaunguforFriMkVedtakDto;
import no.nav.dolly.synt.aap.dto.Vedtak115Dto;
import no.nav.dolly.synt.aap.dto.VedtakRequestDto;
import no.nav.dolly.synt.aap.onnx.OnnxService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Syntetisering")
@Slf4j
class AapVedtakController {

    private final OnnxService onnxService;

    @PostMapping(value = "/aap", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_aap",
            summary = "Generer syntetiske AAP-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AapVedtakDto.class)))
            )
    )
    List<AapVedtakDto> generateAap(
            @RequestBody List<VedtakRequestDto> requests,
            @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {

        var generated = onnxService.generateAap(requests, brukInnsendtTilDato);
        log.info("Generated {} result(s) for /aap", generated.size());
        return generated;

    }

    @PostMapping(value = "/aap/filtered", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_filtered_synthetic_aap",
            summary = "Generer syntetiske AAP-vedtak. Filtrerer ut ugyldige/ulogiske sekvenser i request.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AapVedtakDto.class)))
            )
    )
    List<AapVedtakDto> generateFilteredAap(
            @RequestBody List<VedtakRequestDto> requests,
            @RequestParam(name = "tilDato", defaultValue = "true") boolean brukInnsendtTilDato) {

        var generated = onnxService.generateFilteredAap(requests, brukInnsendtTilDato);
        log.info("Generated {} result(s) for /aap/filtered", generated.size());
        return generated;

    }

    @PostMapping(value = "/11_5", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_11_5",
            summary = "Generer syntetiske 11.5-vedtak.",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Vedtak115Dto.class)))
            )
    )
    List<Vedtak115Dto> generate115(@RequestBody List<VedtakRequestDto> requests) {

        var generated = onnxService.generate115(requests);
        log.info("Generated {} result(s) for /11_5", generated.size());
        return generated;

    }

    @PostMapping(value = "/fri_mk", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_fri_mk",
            summary = "Generer syntetiske Fritak fra å levere meldekort-vedtak (fri_mk).",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AatforAaunguforFriMkVedtakDto.class)))
            )
    )
    List<AatforAaunguforFriMkVedtakDto> generateFriMk(@RequestBody List<VedtakRequestDto> requests) {

        var generated = onnxService.generateFriMk(requests);
        log.info("Generated {} result(s) for /fri_mk", generated.size());
        return generated;

    }

    @PostMapping(value = "/aaungufor", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_aaungufor",
            summary = "Generer syntetiske Ung ufør-vedtak (aaungufor).",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AatforAaunguforFriMkVedtakDto.class)))
            )
    )
    List<AatforAaunguforFriMkVedtakDto> generateAaungufor(@RequestBody List<VedtakRequestDto> requests) {

        var generated = onnxService.generateAaungufor(requests);
        log.info("Generated {} result(s) for /aaungufor", generated.size());
        return generated;

    }

    @PostMapping(value = "/aatfor", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "service.generator.get_synthetic_aatfor",
            summary = "Generer syntetiske Tvungen forvaltning-vedtak (aatfor).",
            security = @SecurityRequirement(name = "jwt", scopes = {"secret"}),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Returnerer liste med syntetiske vedtak.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AatforAaunguforFriMkVedtakDto.class)))
            )
    )
    List<AatforAaunguforFriMkVedtakDto> generateAatfor(@RequestBody List<VedtakRequestDto> requests) {

        var generated = onnxService.generateAatfor(requests);
        log.info("Generated {} result(s) for /aatfor", generated.size());
        return generated;

    }
}
