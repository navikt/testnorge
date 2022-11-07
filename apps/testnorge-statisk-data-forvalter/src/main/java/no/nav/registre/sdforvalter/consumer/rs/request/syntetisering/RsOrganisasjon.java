package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsOrganisasjon extends RsAktoer {

    private String orgnummer;
}
