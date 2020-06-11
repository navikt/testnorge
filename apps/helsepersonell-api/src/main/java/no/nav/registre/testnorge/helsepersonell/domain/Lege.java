package no.nav.registre.testnorge.helsepersonell.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeDTO;

@Slf4j
public class Lege {
    private final CompletableFuture<Samhandler> samhandlerFuture;
    private final CompletableFuture<Persondata> persondataFuture;

    public Lege(CompletableFuture<Samhandler> samhandlerFuture, CompletableFuture<Persondata> persondataFuture) {
        this.samhandlerFuture = samhandlerFuture;
        this.persondataFuture = persondataFuture;
    }

    public LegeDTO toDTO() {
        try {
            Samhandler samhandler = samhandlerFuture.get();
            Persondata persondata = persondataFuture.get();
            return LegeDTO.builder()
                    .fnr(persondata.getFnr())
                    .fornavn(persondata.getFornvan())
                    .mellomnavn(persondata.getMellomnavn())
                    .etternavn(persondata.getEtternavn())
                    .hprId(samhandler.getHprId())
                    .build();
        } catch (Exception e) {
            log.error("Klarer ikke å hente samhandler/persondata", e);
            throw new RuntimeException("Feil ved opprettelse av lege");
        }
    }
}