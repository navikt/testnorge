package no.nav.identpool.ident.ajourhold.tps.generator;

import java.time.LocalDate;

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

    private int antall;
}
