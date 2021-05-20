package no.nav.pdl.forvalter.domain.falskidentitet;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PdlFalskIdentitet extends PdlDbVersjon {

    private Boolean erFalsk;
    private PdlRettIdentitet rettIdentitet;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlRettIdentitet implements Serializable {

        private Boolean rettIdentitetErUkjent;
        private String rettIdentitetVedIdentifikasjonsnummer;
        private PdlRettIdentitetVedOpplysninger rettIdentitetVedOpplysninger;
    }
}
