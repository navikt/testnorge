package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.BARN;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.EKTEFELLE;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.FAR;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.FOEDSEL;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.MOR;
import static no.nav.testnav.libs.dto.tpsmessagingservice.v1.RelasjonDTO.ROLLE.PARTNER;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelasjonDTO {

    private PersonDTO person;
    private PersonDTO personRelasjonMed;
    private String relasjonTypeNavn;

    @JsonIgnore
    public boolean isPartner() {
        return PARTNER.name().equals(getRelasjonTypeNavn()) || EKTEFELLE.name().equals(getRelasjonTypeNavn());
    }

    @JsonIgnore
    public boolean isBarn() {
        return FOEDSEL.name().equals(getRelasjonTypeNavn()) || BARN.name().equals(getRelasjonTypeNavn());
    }

    @JsonIgnore
    public boolean isMor() {
        return MOR.name().equals(getRelasjonTypeNavn());
    }

    @JsonIgnore
    public boolean isFar() {
        return FAR.name().equals(getRelasjonTypeNavn());
    }

    public enum ROLLE {PARTNER, EKTEFELLE, MOR, FAR, FOEDSEL, BARN}
}