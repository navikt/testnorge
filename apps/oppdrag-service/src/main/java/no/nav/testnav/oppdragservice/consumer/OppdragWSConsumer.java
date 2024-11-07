package no.nav.testnav.oppdragservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.oppdragservice.config.ServerProperties;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.xml.transform.dom.DOMResult;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

            if (e instanceof SoapFaultClientException soapFaultClientException) {

                log.error("SoapFaultClientException message: {}, faultCode: {}, faultDetail: {}, faultActorOrRole: {}, faultStringOrReason: {}",
                        soapFaultClientException.getMessage(),
                        soapFaultClientException.getSoapFault().getFaultCode(),
                        StreamSupport.stream(
                                        Spliterators.spliteratorUnknownSize(
                                                soapFaultClientException.getSoapFault().getFaultDetail().getDetailEntries(),
                                                Spliterator.ORDERED), false)
                                .map(SoapFaultDetailElement::getResult)
                                .map(DOMResult.class::cast)
                                .map(result -> "systemId: " + result.getSystemId() +
                                        ", nodeName: " + result.getNode().getNodeName() +
                                        ", nodeValue: " + result.getNode().getNodeValue() +
                                        ", nodeType: " + result.getNode().getNodeType() +
                                        ", nextSibling: " + result.getNextSibling())
                                .collect(Collectors.joining(", ")),
                        soapFaultClientException.getSoapFault().getFaultActorOrRole(),
                        soapFaultClientException.getSoapFault().getFaultStringOrReason(), e);
            }

            throw e;
        }
    }
}
