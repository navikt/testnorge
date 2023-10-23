package no.nav.dolly;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;

@Slf4j
@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
public class DollyBackendApplicationStarter {
    public static void main(String[] args) {

        SpringApplication.run(DollyBackendApplicationStarter.class, args);
    }
}
