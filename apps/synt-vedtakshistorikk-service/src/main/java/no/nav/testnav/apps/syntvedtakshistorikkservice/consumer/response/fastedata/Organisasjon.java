package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.fastedata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organisasjon {
    private String orgnummer;
    private String enhetstype;
    private String navn;
    private String overenhet;
}
