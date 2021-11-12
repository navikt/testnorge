package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2/logout")
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class FrontChannelLogoutDevController {

    private final ReactiveSessionRepository reactiveSessionRepository;

    @GetMapping()
    public void logout(@RequestParam String sid) {

        var sessionsToDelete = reactiveSessionRepository.getAllSessions().stream().filter(session -> {
            var securityContext = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
            if (securityContext == null) {
                return;
            }
            var principal = (DefaultOidcUser) securityContext.getAuthentication().getPrincipal();
            return Objects.equals(sid, principal.getClaims().get("sid"));
        }).toList();

        for (Session session: sessionsToDelete){
            reactiveSessionRepository.deleteById(session.);
        }
    }

}

