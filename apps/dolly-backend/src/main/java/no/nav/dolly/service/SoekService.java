package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Soek;
import no.nav.dolly.repository.SoekRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SoekService {

    private final BrukerService brukerService;
    private final SoekRepository soekRepository;

    public List<Soek> getSoek(Soek.SoekType soekType) {

        var bruker = brukerService.fetchOrCreateBruker();
        return soekRepository.findByBrukerAndSoekType(bruker, soekType).stream()
                .sorted(Comparator.comparing(Soek::getOpprettet).reversed())
                .toList();
    }

    public Soek lagreSoek(Soek.SoekType soekType, String soekVerdi) {

        var bruker = brukerService.fetchOrCreateBruker();
        var eksisterendeSoek = soekRepository.findByBrukerAndSoekType(bruker, soekType).stream()
                .sorted(Comparator.comparing(Soek::getOpprettet))
                .toList();

        if (eksisterendeSoek.size() > 10) {
            soekRepository.delete(eksisterendeSoek.getFirst());
        }

        var soek = Soek.builder()
                .soekVerdi(soekVerdi)
                .bruker(bruker)
                .soekType(soekType)
                .build();

        return soekRepository.save(soek);
    }
}
