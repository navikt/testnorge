package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class MetadataDTO {

    Metadata metadata;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Metadata {

        String opplysningsId;
    }
}
