package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PensjonTestdataResponseDetails {

    private HttpStatus httpStatus;
    private String message;
    private String path;
    private ZonedDateTime timestamp;
}
