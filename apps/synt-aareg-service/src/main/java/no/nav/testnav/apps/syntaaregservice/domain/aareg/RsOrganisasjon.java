package no.nav.testnav.apps.syntaaregservice.domain.aareg;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsOrganisasjon extends RsAktoer {

    private String orgnummer;
}
