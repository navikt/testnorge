package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.personSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {
    String ident;
    String aktorId;
    List<String> tags;
    FoedselDTO foedsel;
}
