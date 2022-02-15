package no.nav.registre.testnorge.profil.provider;

import lombok.SneakyThrows;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import no.nav.registre.testnorge.profil.service.ProfilService;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;

@RestController
@RequestMapping("/api/v1/profil")
public class ProfilController {

    private final ProfilService profilService;
    private final CacheControl cacheControl;

    public ProfilController(ProfilService profilService) {
        this.profilService = profilService;
        this.cacheControl = CacheControl.maxAge(30, TimeUnit.MINUTES).noTransform().mustRevalidate();
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<ProfilDTO> getProfile() {
        var profil = profilService.getProfile();
        return ResponseEntity.ok().cacheControl(cacheControl).body(profil.toDTO());
    }

    @GetMapping(value = "/bilde", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getImage() {
        return profilService
                .getImage()
                .map(value -> ResponseEntity.ok().cacheControl(cacheControl).body(value))
                .orElse(ResponseEntity.notFound().build());
    }
}
