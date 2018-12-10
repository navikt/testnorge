package no.nav.dolly.handler;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class DollyResponseErrorHandler implements ResponseErrorHandler {

    @Override public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return false;
    }

    /**
     * Disable default error handling which masks out cause
     * @param clientHttpResponse
     * @throws IOException
     */
    @Override public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        //Implementation is intentionally blank
    }
}