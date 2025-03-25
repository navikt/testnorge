package no.nav.testnav.libs.reactivecore.logging;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Map;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.stream.Collectors.joining;

@Slf4j
@Component
public class WebClientLogger implements WebClientCustomizer {

    @Override
    public void customize(WebClient.Builder webClientBuilder) {

        var httpClient = HttpClient.create()
                .doOnRequest((httpClientRequest, connection) -> connection.addHandlerFirst(new LoggingHandler()));
        webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    private static class LoggingHandler extends ChannelDuplexHandler {

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof FullHttpRequest request) {
                log.debug("DOWNSTREAM REQUEST: METHOD: {}, URI: {}, BODY: {}, HEADERS: {}",
                        request.method(), request.uri(), request.content().toString(defaultCharset()),
                        tokenFilter(request.headers().entries()));
            } else if (msg instanceof HttpRequest request) {
                log.debug("DOWNSTREAM  REQUEST: METHOD: {}, URI: {}, HEADERS: {}",
                        request.method(), request.uri(), tokenFilter(request.headers().entries()));
            } else if (msg instanceof FullHttpMessage message) {
                log.debug("DOWNSTREAM  REQUEST: BODY: {}",
                        message.content().toString(defaultCharset()));
            }
            super.write(ctx, msg, promise);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpResponse response) {
                log.debug("DOWNSTREAM RESPONSE: STATUS: {}, BODY: {}, HEADERS: {}",
                        response.status().code(), response.content().toString(defaultCharset()), response.headers());
            } else if (msg instanceof HttpResponse response) {
                log.debug("DOWNSTREAM RESPONSE: STATUS: {}, HEADERS: {}",
                        response.status().code(), response.headers());
            } else if (!(msg instanceof LastHttpContent) && msg instanceof HttpContent httpContent) {
                log.debug("DOWNSTREAM RESPONSE: BODY: {}",
                        httpContent.content().toString(defaultCharset()));
            }
            super.channelRead(ctx, msg);
        }
    }

    private static String tokenFilter(List<Map.Entry<String, String>> headers) {
        return headers.stream()
                .map(header -> header.getKey() + ": " +
                        (header.getValue().startsWith("Bearer ") ? "Bearer ******" : header.getValue()))
                .collect(joining(", "));
    }
}