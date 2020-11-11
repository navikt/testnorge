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
import no.nav.registre.testnorge.identservice.testdata.transformation.TransformationService;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TpsRequestService {

    private final XmlMapper xmlMapper;

    private final MessageQueueServiceFactory messageQueueServiceFactory;

    private final TransformationService transformationService;

    public Response executeServiceRutineRequest(TpsServiceRoutineRequest tpsRequest, TpsServiceRoutineDefinitionRequest serviceRoutine, TpsRequestContext context, long timeout)
            throws JMSException, IOException {

        MessageQueueConsumer messageQueueConsumer =
                messageQueueServiceFactory.createMessageQueueConsumer("q2", serviceRoutine.getConfig().getRequestQueue(), false);

        tpsRequest.setServiceRutinenavn(removeTestdataFromServicerutinenavn(tpsRequest.getServiceRutinenavn()));
        String xml = xmlMapper.writeValueAsString(tpsRequest);

        Request request = new Request(xml, tpsRequest, context);
        transformationService.transform(request, serviceRoutine);

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
