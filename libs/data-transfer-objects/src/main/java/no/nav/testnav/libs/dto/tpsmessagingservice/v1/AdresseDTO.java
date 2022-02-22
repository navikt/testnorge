package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AdresseDTO {

    private String kommunenr;
    private LocalDateTime flyttedato;
    private LocalDateTime gyldigTilDato;
    private String postnr;
    private String tilleggsadresse;
    private String bolignr;
    private Boolean deltAdresse;
    private String matrikkelId;
    private Adressetype adressetype;

    public enum Adressetype {GATE, MATR}
}
