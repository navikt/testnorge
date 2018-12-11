package no.nav.dolly.handler;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class DollyResponseErrorHandler implements ResponseErrorHandler {

    @Override public boolean hasError(ClientHttpResponse clientHttpResponse) {
        return false;
    }

    /**
     * Disable default error handling which masks out message
     * @param clientHttpResponse
     * @throws IOException
     */
    @Override public void handleError(ClientHttpResponse clientHttpResponse) {
        //Implementation is intentionally blank
    }
}