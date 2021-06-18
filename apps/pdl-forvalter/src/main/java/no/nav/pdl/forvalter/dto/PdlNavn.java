package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.DbVersjonDTO;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlNavn extends DbVersjonDTO {

    private String etternavn;
    private String forkortetNavn;
    private String fornavn;
    private String mellomnavn;
    private OriginaltNavn originaltNavn;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OriginaltNavn implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}
