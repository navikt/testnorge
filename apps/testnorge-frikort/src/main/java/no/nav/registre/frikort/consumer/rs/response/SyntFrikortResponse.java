package no.nav.registre.frikort.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SyntFrikortResponse {

    String betalt;
    String dato_mottatt;
    String datotjeneste;
    String datotjenestestart;
    double egenandelsats;
    String egenandelskode;
    String enkeltregningsstatuskode;
    String innsendingstypekode;
    String kildesystemkode;
    String merknader;
    String samhandlertypekode;

}
