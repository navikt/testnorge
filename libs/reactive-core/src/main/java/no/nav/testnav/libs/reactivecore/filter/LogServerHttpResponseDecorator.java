package no.nav.testnav.libs.reactivecore.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

/**
 * Waring: Ikke bruk der tokens er inkludert i body.
 */
@Slf4j
public class LogServerHttpResponseDecorator extends ServerHttpResponseDecorator {
    private final ServerHttpRequest serverHttpRequest;
    private final LogResponse logResponse;

    public LogServerHttpResponseDecorator(ServerHttpResponse delegate, ServerHttpRequest request, LogResponse logResponse) {
        super(delegate);
        this.serverHttpRequest = request;
        this.logResponse = logResponse;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        Flux<DataBuffer> buffer = Flux.from(body);
        return super.writeWith(buffer.publishOn(Schedulers.boundedElastic()).doOnNext(dataBuffer -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                Channels.newChannel(byteArrayOutputStream).write(dataBuffer.toByteBuffer().asReadOnlyBuffer());
                logResponse.log(getDelegate(), serverHttpRequest, byteArrayOutputStream.toString(StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("Feil med logging av request.", e);
            }
        }));
    }
}
