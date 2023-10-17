package no.nav.brregstub.api.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
