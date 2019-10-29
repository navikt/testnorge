package no.nav.dolly;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.ProxySelector;

@SpringBootApplication
public class ApplicationConfig {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
                        .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                        .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectTimeout(5000)
                                .setSocketTimeout(5000)
                                .setConnectionRequestTimeout(5000)
                                .build())
                        .setMaxConnPerRoute(2000)
                        .setMaxConnTotal(5000)
                        .build()))
                .build();
    }
}
