package no.nav.registre.testnorge.identservice.testdata.consumers.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessageQueueConsumerConstants {

    /* Used to create a channel name by combining it with the environment name. E.g. 'T1_TPSWS'*/

    public static final String SEARCH_ENVIRONMENT = "q2";
}
