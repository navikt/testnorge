package no.nav.registre.testnorge.identservice.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.testdata.consumers.MessageQueueConsumer;
import no.nav.registre.testnorge.identservice.testdata.factories.MessageQueueServiceFactory;
import no.nav.registre.testnorge.identservice.testdata.response.Response;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.Request;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.io.IOException;

import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.SEARCH_ENVIRONMENT;

@Service
@RequiredArgsConstructor
public class TpsRequestService {

    private static final String XML_PROPERTIES_PREFIX  = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><tpsPersonData xmlns=\"http://www.rtv.no/NamespaceTPS\">";
    private static final String XML_PROPERTIES_POSTFIX = "</tpsPersonData>";

    private final XmlMapper xmlMapper;

    private final MessageQueueServiceFactory messageQueueServiceFactory;

    public Response executeServiceRutineRequest(TpsServiceRoutineRequest tpsRequest, TpsServiceRoutineDefinitionRequest serviceRoutine, TpsRequestContext context, long timeout)
            throws JMSException, IOException {

        MessageQueueConsumer messageQueueConsumer =
                messageQueueServiceFactory.createMessageQueueConsumer(SEARCH_ENVIRONMENT, serviceRoutine.getConfig().getRequestQueue(), false);

        tpsRequest.setServiceRutinenavn(removeTestdataFromServicerutinenavn(tpsRequest.getServiceRutinenavn()));
        String xml = xmlMapper.writeValueAsString(tpsRequest);

        Request request = new Request(xml, tpsRequest, context);
        request.setXml(XML_PROPERTIES_PREFIX + request.getXml() + XML_PROPERTIES_POSTFIX);

        String responseXml = messageQueueConsumer.sendMessage(request.getXml(), timeout);

        Response response = new Response(responseXml, context, serviceRoutine);
        transformationService.transform(response, serviceRoutine);

        return response;
    }

    private String removeTestdataFromServicerutinenavn(String serviceRutinenavn) {

        if (serviceRutinenavn.endsWith("TESTDATA")) {
            return serviceRutinenavn.replace("-TESTDATA", "");
        }
        return serviceRutinenavn;
    }

}
