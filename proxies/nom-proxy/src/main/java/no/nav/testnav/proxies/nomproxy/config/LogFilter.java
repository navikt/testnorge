package no.nav.testnav.proxies.nomproxy.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class LogFilter {

    @Bean
    HttpClient httpClient() {
        return HttpClient.create().wiretap("LoggingFilter",
                LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL);
    }
}
