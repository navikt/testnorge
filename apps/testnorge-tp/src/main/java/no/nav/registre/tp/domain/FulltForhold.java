package no.nav.registre.tp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FulltForhold {

    private Person person;
    private Forhold forhold;
    private Ytelse ytelse;
}
