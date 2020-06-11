package no.nav.registre.arena.core.consumer.rs.util;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;

@Getter
public class ArbeidssoekerCacheElement {

    private LocalDateTime sistOppdatert;
    private Set<Arbeidsoeker> arbeidssoekere;

    public ArbeidssoekerCacheElement(List<Arbeidsoeker> arbeidssoekere) {
        this.sistOppdatert = LocalDateTime.now();
        this.arbeidssoekere = new HashSet<>(arbeidssoekere);
    }
}
