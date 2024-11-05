package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteStatus {

    private HttpStatus status;
}
