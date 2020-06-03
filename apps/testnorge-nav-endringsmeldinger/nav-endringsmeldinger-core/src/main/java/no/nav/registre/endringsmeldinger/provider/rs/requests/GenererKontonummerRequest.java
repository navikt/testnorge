package no.nav.registre.endringsmeldinger.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenererKontonummerRequest {

    private List<String> identer;
    private String kontonummer;
    private String miljoe;
}
