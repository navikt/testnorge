package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Soek;
import no.nav.dolly.repository.SoekRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoekService {

    private final BrukerService brukerService;
    private final SoekRepository soekRepository;

    public Flux<Soek> getSoek(Soek.SoekType soekType) {

        return brukerService.fetchOrCreateBruker()
                .flatMapMany(bruker -> soekRepository.findByBrukerIdAndSoekTypeOrderByIdDesc(bruker.getId(), soekType));
    }

    public Mono<Soek> lagreSoek(Soek.SoekType soekType, String soekVerdi) {

        return brukerService.fetchOrCreateBruker()
                .flatMap(bruker -> Mono.just(Soek.builder()
                        .soekVerdi(soekVerdi)
                        .brukerId(bruker.getId())
                        .soekType(soekType)
                        .opprettetTidspunkt(now())
                        .build()))
                .flatMap(soek -> soekRepository.existsByBrukerIdAndSoekTypeAndSoekVerdi(soek.getBrukerId(), soekType, soekVerdi)
                        .flatMap(eksisterendeSoek -> isFalse(eksisterendeSoek) ?
                                soekRepository.findByBrukerIdAndSoekTypeOrderByIdDesc(soek.getBrukerId(), soekType)
                                        .collectList()
                                        .flatMap(liste -> {
                                            if (liste.size() > 9) {
                                                return soekRepository.delete(liste.getLast())
                                                        .then(soekRepository.save(soek));
                                            } else {
                                                return soekRepository.save(soek);
                                            }
                                        }) :
                                Mono.just(soek)));
    }
}