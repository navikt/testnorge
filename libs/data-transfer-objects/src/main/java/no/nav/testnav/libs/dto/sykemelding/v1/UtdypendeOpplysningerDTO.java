package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtdypendeOpplysningerDTO {

    private String spmGruppeId;
    private String spmGruppeTekst;
    private List<SvarType> spmSvar;

    public List<SvarType> getSpmSvar() {

        if (isNull(spmSvar)) {
            spmSvar = new ArrayList<>();
        }
        return spmSvar;
    }

    public enum Restriksjon {
        SKJERMET_FOR_ARBEIDSGIVER,
        SKJERMET_FOR_PASIENT,
        SKJERMET_FOR_NAV
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SvarType {

        private String spmId;
        private String spmTekst;
        private Restriksjon restriksjon;
        private String svarTekst;
    }
}
