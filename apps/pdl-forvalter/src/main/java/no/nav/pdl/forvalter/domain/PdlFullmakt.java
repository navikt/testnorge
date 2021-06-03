package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.pdl.forvalter.dto.RsPersonRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlFullmakt extends PdlDbVersjon {

    private RsPersonRequest nyFullmektig;

    private String fullmektig;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
    private List<String> omraader;
}
