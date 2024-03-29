package no.nav.testnav.libs.reactivecore.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

/**
 * Waring: ikke bruk der tokens er inkludert i body.
 */
@Slf4j
public class LogServerHttpRequestDecorator extends ServerHttpRequestDecorator {
    private final LogRequest logRequest;

    public LogServerHttpRequestDecorator(ServerHttpRequest delegate, LogRequest logRequest) {
        super(delegate);
        this.logRequest = logRequest;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return super.getBody().publishOn(Schedulers.boundedElastic()).doOnNext(dataBuffer -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                Channels.newChannel(byteArrayOutputStream).write(dataBuffer.toByteBuffer().asReadOnlyBuffer());
                logRequest.log(getDelegate(), byteArrayOutputStream.toString(StandardCharsets.UTF_8));
            } catch (Exception exception) {
                log.error("Feil med logging av requests.", exception);
            }
        });
    }
}
