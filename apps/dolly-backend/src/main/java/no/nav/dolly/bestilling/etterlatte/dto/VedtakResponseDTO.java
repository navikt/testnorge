package no.nav.dolly.bestilling.etterlatte.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VedtakResponseDTO {

    private Integer status;
    private String noekkel;
}
