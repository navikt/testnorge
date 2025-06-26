package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Soek;
import no.nav.dolly.repository.SoekRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

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
        var soek = Soek.builder()
                .soekVerdi(soekVerdi)
                .bruker(bruker)
                .soekType(soekType)
                .build();

        if (isFalse(soekRepository.existsByBrukerAndSoekTypeAndSoekVerdi(bruker, soekType, soekVerdi))) {
            var eksisterendeSoek = soekRepository.findByBrukerAndSoekTypeOrderByIdDesc(bruker, soekType);

            if (eksisterendeSoek.size() > 9) {
                soekRepository.delete(eksisterendeSoek.getLast());
            }
            return soekRepository.save(soek);

        } else {
            return soek;
        }
    }
}
