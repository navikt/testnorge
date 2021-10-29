package no.nav.dolly;

import no.nav.dolly.config.ForkJoinWorkerThreadFactory;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.ProxySelector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

@SpringBootApplication
@Import({ ApplicationCoreConfig.class,
        VaultHikariConfiguration.class,
        SecureOAuth2ServerToServerConfiguration.class })
public class ApplicationConfig {

    private static final int TIMEOUT = 300_000;
    private static final int THREADS_COUNT = 10;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
                        .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                        .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectTimeout(TIMEOUT)
                                .setSocketTimeout(TIMEOUT)
                                .setConnectionRequestTimeout(TIMEOUT)
                                .build())
                        .setMaxConnPerRoute(2000)
                        .setMaxConnTotal(5000)
                        .build()))
                .build();
    }

    @PostConstruct
    public void enableAuthCtxOnSpawnedThreads() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public ExecutorService dollyForkJoinPool() {
        return new DelegatingSecurityContextExecutorService(new ForkJoinPool(THREADS_COUNT, new ForkJoinWorkerThreadFactory(), null, true));
    }
}