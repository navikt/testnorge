//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.core.session.SessionInformation;
//import org.springframework.security.core.session.SessionRegistry;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Objects;
//
//@RestController
//@RequestMapping("/oauth2/logout")
//@RequiredArgsConstructor
//@Profile("prod")
//public class FrontChannelLogoutProdController {
//
//    private final SessionRegistry sessionRegistry;
//
//    @GetMapping()
//    public void logout(@RequestParam String sid) {
//        if (sid==null) return;
//
//        var sessionsToDelete = sessionRegistry.getAllSessions().stream().filter(sessionInformation -> {
//            var principal = (DefaultOidcUser) sessionInformation.getPrincipal();
//            return Objects.equals(sid, principal.getClaims().get("sid"));
//        }).toList();
//
//        for (SessionInformation info: sessionsToDelete){
//            sessionRegistry.removeSessionInformation(info.getSessionId());
//        }
//    }
//
//}
