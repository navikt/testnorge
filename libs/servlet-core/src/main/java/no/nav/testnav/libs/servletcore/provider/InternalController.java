package no.nav.testnav.libs.servletcore.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    private final Environment env;
    private String html;

    @GetMapping("/isAlive")
    @Operation(hidden = true)
    public ResponseEntity<String> isAlive() {
        return ResponseEntity.ok(body());
    }

    @GetMapping("/isReady")
    @Operation(hidden = true)
    public ResponseEntity<String> isReady() {
        return ResponseEntity.ok(body());
    }

    private synchronized String body() {
        if (html == null) {
            html = "OK";
            var naisAppImage = env.getProperty("NAIS_APP_IMAGE");
            if (naisAppImage != null) {
                var i = naisAppImage.lastIndexOf("-");
                if (i > 0) {
                    var hash = naisAppImage.substring(i + 1);
                    html = "OK - image is <a href=https://github.com/navikt/testnorge/commit/%s>%s</a>".formatted(hash, naisAppImage);
                }
            }
        }
        return html;
    }

}