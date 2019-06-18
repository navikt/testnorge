package no.nav.registre.arena.core.service;


import lombok.extern.slf4j.Slf4j;

import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.provider.rs.requests.NyeBrukereRequest;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaForvalterRequest;

import no.nav.registre.arena.domain.NyBruker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class SyntetiseringService {

    private static final String KVALIFISERINGSGRUPPE = "IKVAL";
    private static final boolean AUTOMATISK_INNSENDING_AV_MELDEKORT = true;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Autowired
    private Random rand;

    public ResponseEntity finnSyntetiserteMeldinger(SyntetiserArenaForvalterRequest syntetiserArenaForvalterRequest) {
        List<String> utvalgteIdenter = finnLevendeIdenterOverAlder(syntetiserArenaForvalterRequest);

        NyeBrukereRequest nyeBrukereRequest = new NyeBrukereRequest();
        List<NyBruker> utvalgteBrukere = new ArrayList<>();

        if (utvalgteIdenter.size() < syntetiserArenaForvalterRequest.getAntallNyeIdenter()) {
            log.warn("Fant ikke nok ledige identer. Oppretter brukere for {} antall identer.", utvalgteIdenter.size());
        }
        if (utvalgteIdenter.isEmpty()) {
            List<ResponseEntity> tomRespons = new ArrayList<>();
            tomRespons.add(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fant ingen ledige identer i avspillergruppe" + syntetiserArenaForvalterRequest.getAvspillergruppeId()));
            return tomRespons;
        }

        for (String ident : utvalgteIdenter) {
            utvalgteBrukere.add(NyBruker.builder()
                    .personident(ident)
                    .miljoe(syntetiserArenaForvalterRequest.getMiljoe())
                    .kvalifiseringsgruppe(KVALIFISERINGSGRUPPE)
                    .automatiskInnsendingAvMeldekort(AUTOMATISK_INNSENDING_AV_MELDEKORT)
                    .build());
        }

        nyeBrukereRequest.setNyeBrukere(utvalgteBrukere);

        return arenaForvalterConsumer.leggTilSyntetiserteBrukereIArenaForvalter(nyeBrukereRequest);
    }


    private List<String> finnLevendeIdenterOverAlder(SyntetiserArenaForvalterRequest syntetiserArenaForvalterRequest) {
        List<String> alleLevendeIdenterOverAlder = hodejegerenConsumer.finnLevendeIdenterOverAlder(syntetiserArenaForvalterRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = new ArrayList<>(syntetiserArenaForvalterRequest.getAntallNyeIdenter());

        for (int i = 0; i < syntetiserArenaForvalterRequest.getAntallNyeIdenter(); i++) {
            if (!alleLevendeIdenterOverAlder.isEmpty()) {
                utvalgteIdenter.add(alleLevendeIdenterOverAlder.remove(rand.nextInt(alleLevendeIdenterOverAlder.size())));
            }
        }

        return utvalgteIdenter;
    }


}
