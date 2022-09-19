package no.nav.dolly.bestilling.sigrunstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SigrunStubClient implements ClientRegister {

    private final SigrunStubConsumer sigrunStubConsumer;
    private final SigrunStubResponseHandler sigrunStubResponseHandler;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getSigrunstub().isEmpty()) {
            try {
                List<OpprettSkattegrunnlag> skattegrunnlag = mapperFacade.mapAsList(bestilling.getSigrunstub(), OpprettSkattegrunnlag.class);
                skattegrunnlag.forEach(inntektsaar ->
                        inntektsaar.setPersonidentifikator(dollyPerson.getHovedperson()));

                if (!isOpprettEndre) {
                    sigrunStubConsumer.deleteSkattegrunnlag(List.of(dollyPerson.getHovedperson())).block();
                }

                progress.setSigrunstubStatus(
                        sigrunStubResponseHandler.extractResponse(
                                sigrunStubConsumer.createSkattegrunnlag(skattegrunnlag)));

            } catch (RuntimeException e) {
                progress.setSigrunstubStatus(errorStatusDecoder.decodeRuntimeException(e));
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        try {
            sigrunStubConsumer.deleteSkattegrunnlag(identer)
                    .subscribe(response -> log.info("Slettet antall {} identer fra Sigrunstub", response.size()));

        } catch (RuntimeException e) {
            log.error("Feilet å slette identer fra Sigrunstub: ", String.join(", ", identer));
        }
    }
}
