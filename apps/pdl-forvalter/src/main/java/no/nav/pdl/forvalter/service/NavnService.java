package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.domain.PdlNavn;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NavnService extends PdlArtifactService<PdlNavn> {

    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    @Override
    protected void validate(PdlNavn pdlNavn) {

        // validering kommer
    }

    @Override
    protected void handle(PdlNavn pdlNavn) {

        var navn = genererNavnServiceConsumer.getNavn(1);
        if (navn.isPresent()) {
            pdlNavn.setFornavn(navn.get().getAdjektiv());
            pdlNavn.setEtternavn(navn.get().getSubstantiv());
        }
    }

    @Override
    protected void enforceIntegrity(List<PdlNavn> type) {

        // Ingen integritet å ivareta
    }
}
