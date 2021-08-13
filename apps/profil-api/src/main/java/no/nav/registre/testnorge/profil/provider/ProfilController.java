package no.nav.registre.testnorge.profil.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.profil.consumer.AzureAdProfileConsumer;
import no.nav.registre.testnorge.profil.domain.Profil;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;

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
        try {
            byte[] image = azureAdProfileConsumer.getProfilImage();
            return ResponseEntity.ok(image);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bilde ikke funnet for bruker", e);
        }
    }
}
