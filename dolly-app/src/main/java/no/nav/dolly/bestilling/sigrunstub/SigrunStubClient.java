package no.nav.dolly.bestilling.sigrunstub;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;

@Service
public class SigrunStubClient implements ClientRegister {

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    @Autowired
    private SigrunStubResponseHandler sigrunStubResponseHandler;

    @Override public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (!bestilling.getSigrunstub().isEmpty()) {
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

        identer.forEach(ident -> sigrunStubConsumer.deleteSkattegrunnlag(ident));
    }
}
