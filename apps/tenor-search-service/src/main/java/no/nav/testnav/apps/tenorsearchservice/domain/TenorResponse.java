package no.nav.testnav.apps.tenorsearchservice.domain;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorResponse {

    private String errorMessage;
    private HttpStatus status;
    private JsonNode data;
}