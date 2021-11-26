package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonRequestDTO implements Serializable {

    // All fields are optional
    private Identtype identtype;
    private KjoennDTO.Kjoenn kjoenn;
    private LocalDateTime foedtEtter;
    private LocalDateTime foedtFoer;
    private Integer alder;
    private Boolean syntetisk;

    private NyttNavnDTO nyttNavn;
    private String statsborgerskapLandkode;
    private AdressebeskyttelseDTO.AdresseBeskyttelse gradering;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NyttNavnDTO implements Serializable {

        private boolean hasMellomnavn;
    }
}
