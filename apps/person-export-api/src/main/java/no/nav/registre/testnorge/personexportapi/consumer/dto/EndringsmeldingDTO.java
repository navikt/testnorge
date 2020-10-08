package no.nav.registre.testnorge.personexportapi.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EndringsmeldingDTO {
    String fodselsdato;
    String personnummer;
    String fornavn;
    String mellomnavn;
    String slektsnavn;
}