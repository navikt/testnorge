package no.nav.registre.sdForvalter.consumer.rs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
//TODO: Test SAM
@ContextConfiguration(classes = {SamConsumer.class, RestTemplate.class})
public class SamConsumerTest {

    @Test
    public void findFnrs() {

    }

    @Test
    public void send() {
    }
}