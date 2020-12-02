package no.nav.registre.testnorge.endringsmeldingservice.consumer.dto;

import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

import no.nav.registre.testnorge.libs.dto.endringsmelding.v1.DoedsmeldingDTO;
import no.nav.registre.testnorge.libs.dto.endringsmelding.v1.Handling;

@Value
public class TpsDoedsmeldingDTO {

    String ident;
    String handling;
    LocalDate doedsdato;
    Set<String> miljoer;

    public TpsDoedsmeldingDTO(DoedsmeldingDTO dto, Set<String> miljoer) {
        ident = dto.getIdent();
        handling = convert(dto.getHandling());
        doedsdato = dto.getDoedsdato();
        this.miljoer = miljoer;
    }

    private static String convert(Handling handling) {
        if (handling == null) {
            return null;
        }
        switch (handling) {
            case SETTE_DOEDSDATO:
                return "C";
            case ENDRET_DOEDSDATO:
                return "U";
            case ANNULLERE_DOEDSDATO:
                return "D";
            default:
                return null;
        }
    }

}
