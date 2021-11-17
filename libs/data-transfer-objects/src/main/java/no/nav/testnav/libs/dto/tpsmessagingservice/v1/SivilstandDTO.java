package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SivilstandDTO {

    private PersonDTO person;

    private String sivilstand;

    private LocalDateTime sivilstandRegdato;

    private PersonDTO personRelasjonMed;


    public boolean isSivilstandGift() {

        switch (SivilstatusDTO.lookup(getSivilstand())) {
            case GIFT:
            case SEPARERT:
            case REGISTRERT_PARTNER:
            case SEPARERT_PARTNER:
                return true;
            default:
                return false;
        }
    }
}