package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PensjonTestdataResponseDetails {

    private HttpStatus httpStatus;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
