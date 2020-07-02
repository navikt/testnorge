package no.nav.registre.frikort.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.frikort.domain.xml.Egenandelskode;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SyntFrikortResponse {

    String betalt;
    String dato_mottatt;
    String datotjeneste;
    String datotjenestestart;
    double egenandelsats;
    Egenandelskode egenandelskode;
    String enkeltregningsstatuskode;
    String innsendingstypekode;
    String kildesystemkode;
    String merknader;
    String samhandlertypekode;

}
