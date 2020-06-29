package no.nav.registre.populasjoner.kafka.folkeregisterperson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Folkeregisteridentifikator {

    private String identifikasjonsnummer;
    private Persontype type;
    private Personstatus status;
    private Folkeregistermetadata folkeregistermetadata;
    private Metadata metadata;
}
