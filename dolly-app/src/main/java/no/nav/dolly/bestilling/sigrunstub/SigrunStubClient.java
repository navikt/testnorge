package no.nav.dolly.bestilling.sigrunstub;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SigrunStubClient implements ClientRegister {

    private final SigrunStubConsumer sigrunStubConsumer;
    private final SigrunStubResponseHandler sigrunStubResponseHandler;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (bestilling.getSigrunstub() == null || bestilling.getSigrunstub().isEmpty()) {
            progress.setSigrunstubStatus(null);
            return;
        }

        try {
            for (RsOpprettSkattegrunnlag request : bestilling.getSigrunstub()) {
                request.setPersonidentifikator(norskIdent.getIdent());
            }

            deleteExistingSkattegrunnlag(bestilling.getSigrunstub().get(0).getPersonidentifikator());

            progress.setSigrunstubStatus(
                    sigrunStubResponseHandler.extractResponse(
                            sigrunStubConsumer.createSkattegrunnlag(bestilling.getSigrunstub())));

        } catch (RuntimeException e) {
            progress.setSigrunstubStatus("Feil:" + e.getMessage());
        }
    }

    private void deleteExistingSkattegrunnlag(String ident) {
        try {
            // Alle skattegrunnlag har samme ident
            sigrunStubConsumer.deleteSkattegrunnlag(ident);

        } catch (HttpClientErrorException error) {
            if (!HttpStatus.NOT_FOUND.equals(error.getStatusCode())) {
                throw error;
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteExistingSkattegrunnlag);
    }
}
