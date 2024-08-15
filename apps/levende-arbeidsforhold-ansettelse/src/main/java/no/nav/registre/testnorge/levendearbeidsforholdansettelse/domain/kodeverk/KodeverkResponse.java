package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KodeverkResponse {
    public String kode;
    public String yrke;
}
