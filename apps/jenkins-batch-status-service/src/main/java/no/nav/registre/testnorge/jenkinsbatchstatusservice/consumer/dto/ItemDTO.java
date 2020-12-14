package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;


@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDTO {
    private final Executable executable;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    private static class Executable {
        Long number;
    }

    public Long getNumber() {
        return executable.getNumber();
    }
}