package no.nav.registre.testnorge.originalpopulasjon.consumer.command.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class GetIdenterRequest {
        Integer antall;
        LocalDate foedtEtter;
        LocalDate foedtFoer;
        String identtype;
        String rekvirertAv;
}

