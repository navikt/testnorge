package no.nav.identpool.ident.ajourhold.tps.generator;

import java.time.LocalDate;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonKriterier {

    private Kjoenn kjonn;
    @NotNull
    private LocalDate foedtEtter;
    private LocalDate foedtFoer;
    private Identtype identtype;

    @Max(160)
    private int antall;
}
