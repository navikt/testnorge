package no.nav.registre.sdforvalter.domain;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.consumer.rs.dto.KodeverkDTO;

public class Kodeverk {
    private final static Random RANDOM = new Random();
    private final List<Kode> koder;

    public Kodeverk(KodeverkDTO dto) {
        koder = dto
                .getKoder()
                .stream()
                .map(Kode::new)
                .collect(Collectors.toList());
    }

    public Kode randomKode() {
        return koder.get(RANDOM.nextInt(koder.size()));
    }

    public Optional<Kode> getKodeByName(String name) {
        return koder
                .stream()
                .filter(value -> name.equals(value.getNavn()))
                .findFirst();
    }
}
