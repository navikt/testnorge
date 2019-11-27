package no.nav.dolly.consumer.pdlperson;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlPerson {

    private List<TilrettelagtKommunikasjon> tilrettelagtKommunikasjon;
}
