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
public class Relasjon {

    private String ident;
    private String type;
    private String status;
    private String rolle;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate fraDato;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate tilDato;
}
