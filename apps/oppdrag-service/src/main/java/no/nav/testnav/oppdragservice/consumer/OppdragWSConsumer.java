package no.nav.testnav.oppdragservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.oppdragservice.config.ServerProperties;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragFeilUnderBehandling;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Slf4j
@RequiredArgsConstructor
public class OppdragWSConsumer extends WebServiceGatewaySupport {

    private static final String OPPDRAG_URL = "/cics/services/oppdragService";

    private final ServerProperties props;

    public SendInnOppdragResponse sendOppdrag(String miljoe, SendInnOppdragRequest melding) {

        var url = "%s:%s%s".formatted(props.getHost(),
                props.getPorts().get(miljoe), OPPDRAG_URL);

        try {
            return (SendInnOppdragResponse) getWebServiceTemplate()
                    .marshalSendAndReceive(url, melding);

        } catch (Exception e) {

            if (e instanceof SendInnOppdragFeilUnderBehandling sendInnOppdragFeilUnderBehandling) {

                log.error("SendInnOppdragFeilUnderBehandling message: {}, rootCause: {}, errorSource; {}",
                        sendInnOppdragFeilUnderBehandling.getMessage(),
                        sendInnOppdragFeilUnderBehandling.getFaultInfo().getRootCause(),
                sendInnOppdragFeilUnderBehandling.getFaultInfo().getErrorSource(), e);

            } else if (e instanceof SoapFaultClientException soapFaultClientException){

                log.error("SoapFaultClientException message: {}, faultCode: {}, soapfault: {}, faultStringOrReason: {}",
                        soapFaultClientException.getMessage(),
                        soapFaultClientException.getFaultCode(),
                        soapFaultClientException.getSoapFault(),
                        soapFaultClientException.getFaultStringOrReason(), e);
            }

            throw e;
        }
    }
}
