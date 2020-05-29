package no.nav.registre.hodejegeren.provider.rs.requests.skd.innhold;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonInfo {

    private String kjoenn;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate datoFoedt;
    private String foedtLand;
    private String foedtKommune;
    private String status;
}
