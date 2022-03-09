package no.nav.dolly.bestilling.nom;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.consumer.nom.NomConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Order(7)
@Service
@RequiredArgsConstructor
public class NomClient implements ClientRegister {

    private final NomConsumer nomConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getNomData())) {
            if (bestilling.getNomData().isOpprettNavIdent()) {
                try {
                    ResponseEntity<JsonNode> nomResponse = nomConsumer.opprettNomPerson(dollyPerson.getHovedperson());
                    progress.setNomStatus(nomResponse.getStatusCode().is2xxSuccessful() ? "OK" : nomResponse.getStatusCode().getReasonPhrase());

                } catch (RuntimeException e) {

                    progress.setNomStatus(errorStatusDecoder.decodeRuntimeException(e));
                    log.error("Kall til Nom-api feilet: {}", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        throw new NotImplementedException("Release ikke støttet enda");
    }

    private void deleteIdent(String ident) {

        log.warn("Sletting av ident foreløpig ikke støttet av nom-api");
    }
}
