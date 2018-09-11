package no.nav.identpool.batch.domain.tps.generator;

import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonKriterier {

    private Character kjonn;

    private LocalDate foedtEtter;

    private LocalDate foedtFoer;

    @NotNull
    @Min(1)
    @Max(99)
    private int antall;

}
