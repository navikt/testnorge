package no.nav.registre.testnorge.helsepersonellservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellDTO;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class Helsepersonell {
    private final Samhandler samhandler;
    private final Persondata persondata;

    public HelsepersonellDTO toDTO() {
        try {
            return HelsepersonellDTO
                    .builder()
                    .fnr(persondata.getIdent())
                    .fornavn(persondata.getFornavn())
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