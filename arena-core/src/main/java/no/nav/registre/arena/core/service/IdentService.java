package no.nav.registre.arena.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.BrukereArenaForvalterConsumer;
import no.nav.registre.arena.domain.brukere.Arbeidsoeker;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentService {

    private final BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;

    public List<Arbeidsoeker> hentArbeidsoekere(
            String eier,
            String miljoe,
            String personident
    ) {
        return brukereArenaForvalterConsumer.hentArbeidsoekere(personident, eier, miljoe);
    }

    public List<String> slettBrukereIArenaForvalter(
            List<String> identerToDelete,
            String miljoe
    ) {
        List<String> slettedeIdenter = new ArrayList<>();

        for (var personident : identerToDelete) {
            if (brukereArenaForvalterConsumer.slettBruker(personident, miljoe)) {
                slettedeIdenter.add(personident);
            }
        }

        return slettedeIdenter;
    }
}
