package no.nav.registre.sdforvalter.consumer.rs.aareg.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsOrganisasjon extends RsAktoer {

    private String orgnummer;

    @JsonIgnore
    public Organisasjon toOrganisasjon() {
        return Organisasjon.builder()
                .organisasjonsnummer(orgnummer)
                .build();
    }
}
