package no.nav.registre.testnorge.helsepersonell.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeDTO;

@Slf4j
@RequiredArgsConstructor
public class Lege {
    private final Samhandler samhandler;
    private final CompletableFuture<Persondata> persondataFuture;

    public LegeDTO toDTO() {
        try {
            Persondata persondata = persondataFuture.get();
            return LegeDTO.builder()
                    .fnr(persondata.getFnr())
                    .fornavn(persondata.getFornvan())
                    .mellomnavn(persondata.getMellomnavn())
                    .etternavn(persondata.getEtternavn())
                    .hprId(samhandler.getHprId())
                    .build();
        } catch (Exception e) {
            log.error("Klarer ikke å hente persondata", e);
            return null;
        }
    }
}