package no.nav.dolly.database;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Just a simple controller, so that we at least have a functional Spring application.
 * Required to avoid NAIS entering a restart loop.
 */
@RestController
class RedirectToHealthController {

    @GetMapping("/")
    void redirectToHealth(HttpServletResponse response)
            throws IOException {
        response.sendRedirect("/internal/health");
    }

}
