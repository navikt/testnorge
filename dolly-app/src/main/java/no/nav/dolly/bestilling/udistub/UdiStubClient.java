package no.nav.dolly.bestilling.udistub;

import static java.util.Objects.nonNull;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.udistub.UdiForholdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UdiStubClient implements ClientRegister {

	@Autowired
	private UdiStubConsumer udiStubConsumer;

	@Autowired
	private UdiStubResponseHandler udiStubResponseHandler;

	@Autowired
	private MapperFacade mapperFacade;

	@Override
	public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

		if (nonNull(bestilling.getUdistub())) {

			try {
				udiStubConsumer.deleteUdiForhold(progress.getBestillingId(), norskIdent.getIdent());

				UdiForholdRequest udiForholdRequest = mapperFacade.map(bestilling.getUdistub(), UdiForholdRequest.class);
				ResponseEntity<Object> udistubResponse = udiStubConsumer.createUdiForhold(progress.getBestillingId(), udiForholdRequest);

				progress.setUdistubStatus(udiStubResponseHandler.extractResponse(udistubResponse));

			} catch (RuntimeException e) {
				progress.setUdistubStatus("Feil: " + e.getMessage());
				log.error("Kall til UdiStub feilet: {}", e.getMessage(), e);
			}
		}
	}
}
