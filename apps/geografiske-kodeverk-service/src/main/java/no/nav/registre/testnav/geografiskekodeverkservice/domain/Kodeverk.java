package no.nav.registre.testnav.geografiskekodeverkservice.domain;

import lombok.*;

import java.util.Map;

//@Getter
//@Setter
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
//@Builder
public class Kodeverk {

    public Kodeverk(Map.Entry<Object, Object> entry) {
        kode = entry.getKey().toString();
        navn = entry.getValue().toString();
    }

    private String kode;
    private String navn;
}
