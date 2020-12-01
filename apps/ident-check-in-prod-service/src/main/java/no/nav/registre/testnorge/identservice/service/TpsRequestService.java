package no.nav.registre.testnorge.identservice.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;
import no.nav.registre.testnorge.identservice.testdata.factories.MessageQueueServiceFactory;
import no.nav.registre.testnorge.identservice.testdata.response.Response;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.Request;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils.RsTpsResponseMappingUtils;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.io.IOException;

import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.SEARCH_ENVIRONMENT;

@Service
@RequiredArgsConstructor
public class TpsRequestService {

    private static final String XML_PROPERTIES_PREFIX = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><tpsPersonData xmlns=\"http://www.rtv.no/NamespaceTPS\">";
    private static final String XML_PROPERTIES_POSTFIX = "</tpsPersonData>";
    private static final String REQUEST_QUEUE = "TPS_FORESPORSEL_XML_O";

    private final XmlMapper xmlMapper = new XmlMapper();
    private final MessageQueueServiceFactory messageQueueServiceFactory;
    private final RsTpsResponseMappingUtils rsTpsResponseMappingUtils;


    public TpsServiceRoutineResponse executeServiceRutineRequest(TpsServiceRoutineRequest tpsRequest, TpsRequestContext context, long timeout)
            throws JMSException, IOException {

        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        xmlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        MessageQueueConsumer messageQueueConsumer =
                messageQueueServiceFactory.createMessageQueueConsumer(SEARCH_ENVIRONMENT, REQUEST_QUEUE, false);

        tpsRequest.setServiceRutinenavn(removeTestdataFromServicerutinenavn(tpsRequest.getServiceRutinenavn()));
        String xml = xmlMapper.writeValueAsString(tpsRequest);

        Request request = new Request(xml, tpsRequest, context);
        request.setXml(XML_PROPERTIES_PREFIX + request.getXml() + XML_PROPERTIES_POSTFIX);

        String responseXml = messageQueueConsumer.sendMessage(request.getXml(), timeout);
        Response response = new Response(responseXml, context);

        return rsTpsResponseMappingUtils.convertToTpsServiceRutineResponse(response);
    }

    private String removeTestdataFromServicerutinenavn(String serviceRutinenavn) {

        if (serviceRutinenavn.endsWith("TESTDATA")) {
            return serviceRutinenavn.replace("-TESTDATA", "");
        }
        return serviceRutinenavn;
    }

}
