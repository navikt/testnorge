package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullmaktDTO extends DbVersjonDTO {

    private PersonRequestDTO nyFullmektig;

    private String fullmektig;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;
    private List<String> omraader;
}
