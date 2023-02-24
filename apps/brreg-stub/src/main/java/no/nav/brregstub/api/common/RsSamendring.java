package no.nav.brregstub.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RsSamendring {

    @NotNull
    private LocalDate registringsDato;

    @NotEmpty
    @Valid
    private List<RsPersonOgRolle> roller = new LinkedList<>();

}
