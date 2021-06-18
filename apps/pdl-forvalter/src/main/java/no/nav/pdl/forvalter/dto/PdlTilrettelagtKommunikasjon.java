package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.domain.DbVersjonDTO;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlTilrettelagtKommunikasjon extends DbVersjonDTO {

    private Tolk talespraaktolk;
    private Tolk tegnspraaktolk;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tolk implements Serializable {

        private String spraak;
    }
}
