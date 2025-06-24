package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Soek;
import no.nav.dolly.repository.SoekRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SoekService {

    private final BrukerService brukerService;
    private final SoekRepository soekRepository;

    public List<Soek> getSoek(Soek.SoekType soekType) {

        var bruker = brukerService.fetchOrCreateBruker();
        return soekRepository.findByBrukerAndSoekTypeOrderByIdDesc(bruker, soekType);
    }

    public Soek lagreSoek(Soek.SoekType soekType, String soekVerdi) {

        var bruker = brukerService.fetchOrCreateBruker();
        var eksisterendeSoek = soekRepository.findByBrukerAndSoekTypeOrderByIdDesc(bruker, soekType);

        if (eksisterendeSoek.size() > 9) {
            soekRepository.delete(eksisterendeSoek.getLast());
        }

        var soek = Soek.builder()
                .soekVerdi(soekVerdi)
                .bruker(bruker)
                .soekType(soekType)
                .build();

        return soekRepository.save(soek);
    }
}
