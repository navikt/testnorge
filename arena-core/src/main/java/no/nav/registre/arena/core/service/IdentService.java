package no.nav.registre.arena.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.domain.Arbeidsoeker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentService {

    private final ArenaForvalterConsumer arenaForvalterConsumer;


    public List<Arbeidsoeker> hentArbeidsoekere(String eier, String miljoe, String personident) {
        return arenaForvalterConsumer.hentArbeidsoekere(personident, eier, miljoe);
    }

    public List<String> slettBrukereIArenaForvalter(List<String> identerToDelete, String miljoe) {

        List<String> slettedeIdenter = new ArrayList<>();

        for (String personident : identerToDelete) {
            if (arenaForvalterConsumer.slettBrukerSuccessful(personident, miljoe)) {
                slettedeIdenter.add(personident);
            }
        }

        return slettedeIdenter;
    }
}
