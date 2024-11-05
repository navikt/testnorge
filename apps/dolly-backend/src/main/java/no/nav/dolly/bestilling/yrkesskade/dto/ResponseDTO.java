package no.nav.dolly.bestilling.yrkesskade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

    public enum Status {OK, FEIL}

    private Integer id;
    private Status status;
    private String melding;
}
