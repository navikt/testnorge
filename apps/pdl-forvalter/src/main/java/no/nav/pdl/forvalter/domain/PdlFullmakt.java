package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlFullmakt extends PdlDbVersjon {

    private String fullmektig;
    private LocalDate gyldigFom;
    private LocalDate gyldigTom;
    private String kilde;
    private List<String> omraader;

}
