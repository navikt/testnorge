package no.nav.registre.testnorge.synt.sykemelding.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeListeDTO;

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
