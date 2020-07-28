package no.nav.registre.populasjoner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import no.nav.registre.populasjoner.adapter.MiniNorgeIdenterAdapter;
import no.nav.registre.populasjoner.adapter.TenorIdenterAdapter;
import no.nav.registre.populasjoner.domain.Populasjon;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final MiniNorgeIdenterAdapter miniNorgeIdenterAdapter;
    private final TenorIdenterAdapter tenorIdenterAdapter;

    public Set<String> getIdenter(Populasjon populasjon) {

        if (populasjon == Populasjon.MINI_NORGE) {
            return miniNorgeIdenterAdapter.getIdenter();
        } else {
            return tenorIdenterAdapter.getIdenter();
        }
    }
}
