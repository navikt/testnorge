package no.nav.dolly.bestilling.krrstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class KrrstubClient implements ClientRegister {

    private final KrrstubConsumer krrstubConsumer;
    private final KrrstubResponseHandler krrstubResponseHandler;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    private static boolean isKrrMaalform(String spraak) {

        return isNotBlank(spraak) && Stream.of("NB", "NN", "EN", "SE").anyMatch(spraak::equalsIgnoreCase);
    }

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getKrrstub()) ||
                (nonNull(bestilling.getTpsf()) && isKrrMaalform(bestilling.getTpsf().getSprakKode()))) {

            try {
                DigitalKontaktdata digitalKontaktdata = mapperFacade.map(
                        nonNull(bestilling.getKrrstub()) ? bestilling.getKrrstub() : new RsDigitalKontaktdata(),
                        DigitalKontaktdata.class);
                digitalKontaktdata.setPersonident(dollyPerson.getHovedperson());

                kobleMaalformTilSpraak(bestilling, digitalKontaktdata);

                if (!isOpprettEndre) {
                    deleteIdent(dollyPerson.getHovedperson());
                }

                ResponseEntity<Object> krrstubResponse = krrstubConsumer.createDigitalKontaktdata(digitalKontaktdata);
                progress.setKrrstubStatus(krrstubResponseHandler.extractResponse(krrstubResponse));

            } catch (RuntimeException e) {

                progress.setKrrstubStatus(errorStatusDecoder.decodeRuntimeException(e));
                log.error("Kall til KrrStub feilet: {}", e.getMessage(), e);
            }
        }
    }

    private void kobleMaalformTilSpraak(RsDollyUtvidetBestilling bestilling, DigitalKontaktdata digitalKontaktdata) {

        if (nonNull(bestilling.getTpsf()) && isKrrMaalform(bestilling.getTpsf().getSprakKode()) && isBlank(digitalKontaktdata.getSpraak())) {
            digitalKontaktdata.setSpraak(bestilling.getTpsf().getSprakKode().toLowerCase());
            digitalKontaktdata.setSpraakOppdatert(ZonedDateTime.now());
            digitalKontaktdata.setRegistrert(true);
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteIdent);
    }

    private void deleteIdent(String ident) {

        try {
            ResponseEntity<List<DigitalKontaktdata>> response = krrstubConsumer.getDigitalKontaktdata(ident);

            if (response.hasBody()) {
                response.getBody().forEach(dkif -> {
                    if (nonNull(dkif.getId())) {
                        krrstubConsumer.deleteDigitalKontaktdata(dkif.getId());
                    }
                });
            }

        } catch (RuntimeException e) {

            log.error("Feilet å slette ident {} fra KRR-Stub", ident, e);
        }
    }
}
