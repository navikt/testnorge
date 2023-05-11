package no.nav.testnav.libs.servletcore.provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping(
        path = "/internal",
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
@RequiredArgsConstructor
public class InternalController {

    @Value("${NAIS_APP_IMAGE:null}")
    private final String image;

    @GetMapping("/isAlive")
    @Operation(hidden = true)
    public ResponseEntity<HttpStatus> isAlive() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/isReady")
    @Operation(hidden = true)
    public ResponseEntity<HttpStatus> isReady() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/image")
    @Operation(hidden = true)
    @ResponseStatus(HttpStatus.OK)
    public JsonResponse getVersion() {

        return JsonResponse.builder()
                .image(image)
                .commit(nonNull(image) && image.lastIndexOf("-") > 0 ?
                        "https://github.com/navikt/testnorge/commit/" +
                                image.substring(image.lastIndexOf("-") + 1) : null)
                .build();
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    record JsonResponse(
            String image,
            String commit
    ) {
    }
}