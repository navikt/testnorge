package no.nav.registre.testnorge.profil.provider;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

import no.nav.registre.testnorge.profil.service.ProfilService;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.testnav.libs.securitycore.UserInfo;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;

@RestController
@RequestMapping("/api/v1/profil")
@RequiredArgsConstructor
public class ProfilController {

    private final ProfilService profilService;


    @SneakyThrows
    @GetMapping
    public ResponseEntity<ProfilDTO> getProfile() {
        var profil = profilService.getProfile();
        return ResponseEntity.ok(profil.toDTO());
    }

    @GetMapping(value = "/bilde", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getImage() {
        return profilService
                .getImage()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
