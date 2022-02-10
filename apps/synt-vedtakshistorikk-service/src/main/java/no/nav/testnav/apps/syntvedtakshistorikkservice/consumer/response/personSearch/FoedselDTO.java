package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.personSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FoedselDTO {
    LocalDate foedselsdato;
}