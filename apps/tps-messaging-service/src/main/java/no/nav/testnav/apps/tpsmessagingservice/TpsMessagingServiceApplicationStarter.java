package no.nav.testnav.apps.tpsmessagingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;

@SpringBootApplication(exclude = ElasticsearchDataAutoConfiguration.class)
public class TpsMessagingServiceApplicationStarter {

    public static void main(String[] args) {

        SpringApplication.run(TpsMessagingServiceApplicationStarter.class, args);
    }
}
