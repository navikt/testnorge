package no.nav.registre.testnorge.arena.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.BrukereArenaForvalterConsumer;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentService {

    private final BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;

    public List<Arbeidsoeker> hentArbeidsoekere(
            String eier,
            String miljoe,
            String personident,
            boolean useCache
    ) {
        return brukereArenaForvalterConsumer.hentArbeidsoekere(personident, eier, miljoe, useCache);
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
