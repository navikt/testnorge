package no.nav.registre.populasjoner.kafka.folkeregisterperson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folkeregisterperson {

    private List<Folkeregisteridentifikator> folkeregisteridentifikator;
}
