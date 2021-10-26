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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
                    deleteExistingSkattegrunnlag(dollyPerson.getHovedperson());
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

        identer.forEach(this::deleteExistingSkattegrunnlag);
    }

    private void deleteExistingSkattegrunnlag(String ident) {
        try {
            // Alle skattegrunnlag har samme ident
            sigrunStubConsumer.deleteSkattegrunnlag(ident);

        } catch (WebClientResponseException error) {
            if (!HttpStatus.NOT_FOUND.equals(error.getStatusCode())) {

                log.error("Feilet å slette ident {} fra Sigrunstub", ident, error);
            }
        }
    }
}
