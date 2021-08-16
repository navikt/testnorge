package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
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
