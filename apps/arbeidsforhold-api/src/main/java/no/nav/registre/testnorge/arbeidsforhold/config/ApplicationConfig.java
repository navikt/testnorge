package no.nav.registre.testnorge.arbeidsforhold.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;

import no.nav.registre.testnorge.arbeidsforhold.repository.elastic.OppsummeringsdokumentRepository;
import no.nav.registre.testnorge.arbeidsforhold.repository.elastic.model.OppsummeringsdokumentModel;
import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config.DependencyAnalysisAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.AnalysisFSSAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.database.config.FlywayConfiguration;
import no.nav.registre.testnorge.libs.database.config.VaultHikariConfiguration;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Slf4j
@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        FlywayConfiguration.class,
        VaultHikariConfiguration.class,
        SecureOAuth2ServerToServerConfiguration.class,
        DependencyAnalysisAutoConfiguration.class,
        AnalysisFSSAutoConfiguration.class
})
@EnableElasticsearchRepositories(basePackages = "no.nav.registre.testnorge.arbeidsforhold.repository.elastic")
@EnableJpaAuditing
public class ApplicationConfig {

    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }

    @Autowired OppsummeringsdokumentRepository repository;

    @PostConstruct
    public void init(){
        OppsummeringsdokumentModel model = new OppsummeringsdokumentModel();
        model.setTitle("dummy");
        repository.save(model);
        log.info(repository.deleteAll(););
    }

}
