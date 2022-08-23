package no.nav.testnav.apps.hodejegeren.provider.requests.skd.innhold;

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
public class Doedshistorikk {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate dato;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate regDato;
}
