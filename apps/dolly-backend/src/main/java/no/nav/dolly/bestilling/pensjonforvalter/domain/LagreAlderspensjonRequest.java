package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LagreAlderspensjonRequest {

    private List<String> miljoer;

    private String pid;
    private LocalDateTime iverksettelsesdato;
    private Integer uttaksgrad;
    private String statsborgerskap;
}
