package no.nav.registre.testnorge.synt.sykemelding.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;

@Slf4j
public class HelsepersonellListe {
    private final List<Helsepersonell> list;
    private final Random random = new Random();

    public HelsepersonellListe(HelsepersonellListeDTO dto) {
        list = dto.getHelsepersonell()
                .stream()
                .map(Helsepersonell::new)
                .collect(Collectors.toList());
    }

    public Helsepersonell getRandomLege() {
        var helsepersonell = list.get(random.nextInt(list.size()));
        log.info("Valgt tilfeldig helsepersonell {}", helsepersonell.getIdent());
        return helsepersonell;
    }
}
