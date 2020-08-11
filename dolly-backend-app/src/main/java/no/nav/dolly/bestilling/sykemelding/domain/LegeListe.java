package no.nav.dolly.bestilling.sykemelding.domain;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sykemelding.domain.dto.LegeListeDTO;

@Slf4j
public class LegeListe {
    private final List<Lege> list;
    private final Random random = new Random();

    public LegeListe(LegeListeDTO dto) {
        list = dto.getLeger()
                .stream()
                .map(Lege::new)
                .collect(Collectors.toList());
    }

    public Lege getRandomLege() {
        var lege = list.get(random.nextInt(list.size()));
        log.info("Valgt tilfeldig lege {}", lege.getIdent());
        return lege;
    }
}

