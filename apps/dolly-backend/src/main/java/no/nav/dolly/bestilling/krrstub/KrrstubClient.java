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
import no.nav.dolly.util.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class KrrstubClient implements ClientRegister {

    private final KrrstubConsumer krrstubConsumer;
    private final ResponseHandler responseHandler;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    private static boolean isKrrMaalform(String spraak) {

        return isNotBlank(spraak) && Stream.of("NB", "NN", "EN", "SE").anyMatch(spraak::equalsIgnoreCase);
    }

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getKrrstub()) ||
                (nonNull(bestilling.getTpsf()) && isKrrMaalform(bestilling.getTpsf().getSprakKode())) ||
                (nonNull(bestilling.getTpsMessaging()) && isKrrMaalform(bestilling.getTpsMessaging().getSpraakKode()))) {

            try {
                DigitalKontaktdata digitalKontaktdata = mapperFacade.map(
                        nonNull(bestilling.getKrrstub()) ? bestilling.getKrrstub() : new RsDigitalKontaktdata(),
                        DigitalKontaktdata.class);
                digitalKontaktdata.setPersonident(dollyPerson.getHovedperson());

                kobleMaalformTilSpraak(bestilling, digitalKontaktdata);

                if (!isOpprettEndre) {
                    krrstubConsumer.deleteKontaktdata(List.of(dollyPerson.getHovedperson())).block();
                }

                ResponseEntity<Object> krrstubResponse = krrstubConsumer.createDigitalKontaktdata(digitalKontaktdata);
                progress.setKrrstubStatus(responseHandler.extractResponse(krrstubResponse));

            } catch (RuntimeException e) {

                progress.setKrrstubStatus(errorStatusDecoder.decodeRuntimeException(e));
                log.error("Kall til KrrStub feilet: {}", e.getMessage(), e);
            }
        }
    }

    private void kobleMaalformTilSpraak(RsDollyUtvidetBestilling bestilling, DigitalKontaktdata digitalKontaktdata) {

        String maalform = null;

        if (nonNull(bestilling.getTpsf()) && isKrrMaalform(bestilling.getTpsf().getSprakKode())) {
            maalform = bestilling.getTpsf().getSprakKode();

        } else if (nonNull(bestilling.getTpsMessaging()) && isKrrMaalform(bestilling.getTpsMessaging().getSpraakKode())) {
            maalform = bestilling.getTpsMessaging().getSpraakKode();
        }

        if (isNotBlank(maalform) && isBlank(digitalKontaktdata.getSpraak())) {

            digitalKontaktdata.setSpraak(isNotBlank(maalform) ? maalform.toLowerCase() : maalform);
            digitalKontaktdata.setSpraakOppdatert(ZonedDateTime.now());
            digitalKontaktdata.setRegistrert(true);
        }
    }

    @Override
    public void release(List<String> identer) {

        try {
            krrstubConsumer.deleteKontaktdata(identer)
                    .subscribe(resp -> log.info("Slettet antall {} identer fra Krrstub", resp.size()));

        } catch (RuntimeException e) {

            log.error("Feilet å slette identer {} fra KRR-Stub", identer.stream().collect(Collectors.joining(", ")), e);
        }
    }
}
