package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlFullmakt extends PdlDbVersjon {

    private String fullmektig;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
    private List<String> omraader;
}
