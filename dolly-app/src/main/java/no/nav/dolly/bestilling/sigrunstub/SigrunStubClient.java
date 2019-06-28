package no.nav.dolly.bestilling.sigrunstub;

import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SigrunStubClient implements ClientRegister {

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    @Autowired
    private SigrunStubResponseHandler sigrunStubResponseHandler;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (!bestilling.getSigrunstub().isEmpty()) {
            try {
                for (RsOpprettSkattegrunnlag request : bestilling.getSigrunstub()) {
                    request.setPersonidentifikator(norskIdent.getIdent());
                }

                // Alle skattegrunnlag har samme ident
                sigrunStubConsumer.deleteSkattegrunnlag(bestilling.getSigrunstub().get(0).getPersonidentifikator());
                progress.setSigrunstubStatus(
                        sigrunStubResponseHandler.extractResponse(
                                sigrunStubConsumer.createSkattegrunnlag(bestilling.getSigrunstub())));

            } catch (RuntimeException e) {
                progress.setSigrunstubStatus("Feil:" + e.getMessage());
            }
        }
    }
}
