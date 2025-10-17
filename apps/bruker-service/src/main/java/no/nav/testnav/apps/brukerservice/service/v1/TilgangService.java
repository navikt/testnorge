package no.nav.testnav.apps.brukerservice.service.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.brukerservice.dto.TilgangDTO;
import no.nav.testnav.apps.brukerservice.repository.UserEntity;
import no.nav.testnav.apps.brukerservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TilgangService {

    private final UserRepository userRepository;

    public Mono<TilgangDTO> getBrukereISammeOrganisasjon(String brukerId) {

        return userRepository.findBrukereISammeOrganisasjoner(brukerId)
                .mapNotNull(UserEntity::getId)
                .collectList()
                .map(TilgangDTO::new);
    }
}
