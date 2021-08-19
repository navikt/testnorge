package no.nav.testnav.apps.profilapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.profilapi.consumer.AzureAdProfileConsumer;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;

@RestController
@RequestMapping("/api/v1/profil")
@RequiredArgsConstructor
public class ProfilController {

    private final AzureAdProfileConsumer azureAdProfileConsumer;

    @GetMapping
    public Mono<ResponseEntity<ProfilDTO>> getProfile() {
        return azureAdProfileConsumer
                .getProfil()
                .map(profil -> ResponseEntity.ok(profil.toDTO()));
    }

    @GetMapping(value = "/bilde", produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<ResponseEntity<byte[]>> getImage() {
        return azureAdProfileConsumer
                .getProfilImage()
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
