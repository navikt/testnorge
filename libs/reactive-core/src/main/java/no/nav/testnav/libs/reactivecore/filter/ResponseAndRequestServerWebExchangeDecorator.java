package no.nav.testnav.libs.reactivecore.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

public class ResponseAndRequestServerWebExchangeDecorator extends ServerWebExchangeDecorator {

    private final ServerHttpRequestDecorator requestDecorator;
    private final ServerHttpResponseDecorator responseDecorator;

    public ResponseAndRequestServerWebExchangeDecorator(
            ServerWebExchange delegate,
            ServerHttpRequestDecorator requestDecorator,
            ServerHttpResponseDecorator responseDecorator
    ) {
        super(delegate);
        this.requestDecorator = requestDecorator;
        this.responseDecorator = responseDecorator;
    }

    @Override
    public ServerHttpRequest getRequest() {
        return requestDecorator;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return responseDecorator;
    }

}
