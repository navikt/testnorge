package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RsFullmakt extends PdlDbVersjon {

    private RsPersonRequest nyFullmektig;

    private String fullmektig;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
    private List<String> omraader;
}
