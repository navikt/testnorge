package no.nav.registre.arena.core.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import no.nav.registre.arena.domain.brukere.NyBruker;

@AllArgsConstructor
@Getter
public class IdentMedData {

    private String id;
    private List<NyBruker> data;
}
