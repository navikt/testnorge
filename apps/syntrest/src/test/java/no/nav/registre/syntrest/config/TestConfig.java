package no.nav.registre.syntrest.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Profile("test")
@RequiredArgsConstructor
@Import(ApplicationCoreConfig.class)
public class TestConfig {

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        int executorPoolSize = 4;
        return Executors.newScheduledThreadPool(executorPoolSize);
    }

    @Bean
    UriBuilderFactory uriFactory() {
        return new DefaultUriBuilderFactory();
    }

    @Bean
    public WebClient.Builder webClientBuilder() throws SSLException {
        // Accept all CE certificates
        SslContext context = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
