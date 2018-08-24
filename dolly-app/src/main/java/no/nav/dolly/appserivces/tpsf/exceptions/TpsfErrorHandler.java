package no.nav.dolly.appserivces.tpsf.exceptions;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class TpsfErrorHandler implements ResponseErrorHandler{

    @Override public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return clientHttpResponse.getStatusCode() != HttpStatus.OK && clientHttpResponse.getStatusCode() != HttpStatus.CREATED;
    }

    @Override public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {

        String s = "S";
    }
}
