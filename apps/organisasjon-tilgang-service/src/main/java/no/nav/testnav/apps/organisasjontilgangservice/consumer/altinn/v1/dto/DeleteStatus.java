package no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.dto;

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
