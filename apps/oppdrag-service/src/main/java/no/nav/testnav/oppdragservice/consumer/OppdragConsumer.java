package no.nav.testnav.oppdragservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

@Slf4j
@Service
public class OppdragConsumer extends WebServiceGatewaySupport {

    public SendInnOppdragResponse sendOppdrag(SendInnOppdragRequest melding) {

        return (SendInnOppdragResponse) getWebServiceTemplate().marshalSendAndReceive("", melding,
                new SoapActionCallback("no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse"));
    }
}
