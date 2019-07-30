package no.nav.registre.orkestratoren.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.ArenaConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@Service
public class ArenaSyntPakkenService {

    @Autowired
    ArenaConsumer arenaConsumer;

    public List<String> opprettArbeidssokereIArena(SyntetiserArenaRequest arenaRequest) {
        return arenaConsumer.opprettArbeidsoekere(arenaRequest);
    }
}
