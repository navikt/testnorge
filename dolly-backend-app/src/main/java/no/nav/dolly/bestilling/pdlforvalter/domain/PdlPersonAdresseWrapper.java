package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.tpsf.Person;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlPersonAdresseWrapper {

    public enum Adressetype {NORSK, UTENLANDSK}

    private Person person;
    private Adressetype adressetype;
}
