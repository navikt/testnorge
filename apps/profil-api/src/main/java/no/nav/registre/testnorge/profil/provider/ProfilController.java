package no.nav.registre.testnorge.profil.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.registre.testnorge.profil.consumer.AzureAdProfileConsumer;
import no.nav.registre.testnorge.profil.domain.Profil;

@RestController
@RequestMapping("/api/v1/profil")
@RequiredArgsConstructor
public class ProfilController {

    private final AzureAdProfileConsumer azureAdProfileConsumer;

    @GetMapping
    public ResponseEntity<ProfilDTO> getProfile() {
        Profil profil = azureAdProfileConsumer.getProfil();
        return ResponseEntity.ok(profil.toDTO());
    }

    @GetMapping(value = "/bilde", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage() {
        byte[] image = azureAdProfileConsumer.getProfilImage();
        return ResponseEntity.ok(image);
    }
}
