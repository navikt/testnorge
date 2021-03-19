package no.nav.registre.testnorge.helsepersonellservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.HelsepersonellDTO;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class Helsepersonell {
    private final Samhandler samhandler;
    private final CompletableFuture<Persondata> persondataFuture;

    public HelsepersonellDTO toDTO() {
        try {
            Persondata persondata = persondataFuture.get();
            return HelsepersonellDTO
                    .builder()
                    .fnr(persondata.getFnr())
                    .fornavn(persondata.getFornvan())
                    .mellomnavn(persondata.getMellomnavn())
                    .etternavn(persondata.getEtternavn())
                    .hprId(samhandler.getHprId())
                    .samhandlerType(samhandler.getSamhandlerType())
                    .build();
        } catch (Exception e) {
            log.error("Klarer ikke å hente persondata", e);
            return null;
        }
    }
}