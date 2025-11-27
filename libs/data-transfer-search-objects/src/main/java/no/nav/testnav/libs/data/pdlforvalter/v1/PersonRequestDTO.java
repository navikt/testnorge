package no.nav.testnav.libs.data.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDTO implements Serializable {

    // All fields are optional
    private Identtype identtype;
    private KjoennDTO.Kjoenn kjoenn;
    private LocalDateTime foedtEtter;
    private LocalDateTime foedtFoer;
    private Integer alder;
    private Boolean syntetisk;
    private Boolean id2032;

    private NyttNavnDTO nyttNavn;
    private String statsborgerskapLandkode;
    private AdressebeskyttelseDTO.AdresseBeskyttelse gradering;
    private String eksisterendeIdent;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NyttNavnDTO implements Serializable {

        private boolean hasMellomnavn;
    }
}
