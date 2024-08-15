package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Ident {
    private String ident;
    private String gruppe;

    @Override
    public String toString() {
        return "Ident{" +
                "ident='" + ident + '\'' +
                ", gruppe='" + gruppe + '\'' +
                '}';
    }
}
