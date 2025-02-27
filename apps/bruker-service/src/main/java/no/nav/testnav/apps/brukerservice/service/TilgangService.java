package no.nav.testnav.apps.brukerservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.repository.UserEntity;
import no.nav.testnav.apps.brukerservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class TilgangService {

    private final UserRepository userRepository;

    public Flux<String> getBrukereISammeOrganisasjon(String brukeId) {

        return userRepository.findBrukereISammeOrganisasjoner(brukeId)
                .mapNotNull(UserEntity::getId);
    }
}
