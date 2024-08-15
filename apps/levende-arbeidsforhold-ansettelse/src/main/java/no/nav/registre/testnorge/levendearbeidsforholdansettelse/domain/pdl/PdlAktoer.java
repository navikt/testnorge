package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PdlAktoer {
    List<Feilmelding> errors;
    Data data;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Data {
        HentIdenter hentIdenter;
        HentPerson hentPerson;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class HentIdenter {
        List<AktoerIdent> identer;

        public List<AktoerIdent> getIdenter() {
            if (isNull(identer)) {
                identer = new ArrayList<>();
            }
            return identer;
        }
    }

    @Value
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class AktoerIdent {
        String ident;
        Boolean historisk;
        String gruppe;
    }


    public List<Feilmelding> getErrors() {
        return errors == null ? Collections.emptyList() : errors;
    }
}
