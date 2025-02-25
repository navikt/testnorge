package no.nav.testnav.libs.servletcore.health;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * <p>A filter to provide forwards from the legacy {@code /internal/isAlive} and {@code /internal/isReady}
 * endpoints to the new {@code /internal/health/liveness} and {@code /internal/health/readiness} endpoints.</p>
 * <p>This is to ensure backwards compatibility with external apps that may still be using the old endpoints.</p>
 */
class LegacyHealthEndpointsForwardingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        switch (request.getRequestURI()) {
            case "/internal/isAlive" -> request
                        .getRequestDispatcher("/internal/health/liveness")
                        .forward(request, response);
            case "/internal/isReady" -> request
                        .getRequestDispatcher("/internal/health/readiness")
                        .forward(request, response);
            default -> chain.doFilter(request, response);
        }

    }

}