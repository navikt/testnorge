package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@Service
public class TesnorgeArenaService {

    @Autowired
    private TestnorgeArenaConsumer testnorgeArenaConsumer;

    public List<String> opprettArbeidssokereIArena(SyntetiserArenaRequest arenaRequest) {
        return testnorgeArenaConsumer.opprettArbeidsoekere(arenaRequest);
    }
}
