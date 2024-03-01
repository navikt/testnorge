package no.nav.testnav.apps.tpsmessagingservice.service.skd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TpsConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendSkdMeldinger {

    private final TpsConsumer tpsConsumer;

    public Map<String, String> sendMeldinger(String skdMelding , List<String> environments) {

        try {
            return tpsConsumer.sendMessage(skdMelding, environments);

        } catch (JmsException jmsException) {
            log.error(jmsException.getMessage(), jmsException);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, jmsException.getMessage());
        }
    }
}
