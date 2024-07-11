package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Data {
    HentPerson hentPerson;
}
