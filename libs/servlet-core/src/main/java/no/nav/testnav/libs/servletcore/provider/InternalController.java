package no.nav.testnav.libs.servletcore.provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        path = "/internal",
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
@RequiredArgsConstructor
public class InternalController {

    private final Environment env;
    private JsonResponse json;

    @GetMapping("/isAlive")
    @Operation(hidden = true)
    public ResponseEntity<JsonResponse> isAlive() {
        return ResponseEntity.ok(body());
    }

    @GetMapping("/isReady")
    @Operation(hidden = true)
    public ResponseEntity<JsonResponse> isReady() {
        return ResponseEntity.ok(body());
    }

    private synchronized JsonResponse body() {
        if (json == null) {
            json = new JsonResponse("OK", null, null);
            var naisImage = env.getProperty("NAIS_APP_IMAGE");
            if (naisImage != null) {
                var i = naisImage.lastIndexOf("-");
                if (i > 0) {
                    json = new JsonResponse("OK", naisImage, "https://github.com/navikt/testnorge/commit/" + naisImage.substring(i + 1));
                }
            }
        }
        return json;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record JsonResponse(
            String status,
            String image,
            String commit
    ) {
    }

}