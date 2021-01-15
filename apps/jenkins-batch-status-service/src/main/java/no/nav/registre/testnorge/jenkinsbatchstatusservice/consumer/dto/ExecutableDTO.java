package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ExecutableDTO {
    Long number;
}
