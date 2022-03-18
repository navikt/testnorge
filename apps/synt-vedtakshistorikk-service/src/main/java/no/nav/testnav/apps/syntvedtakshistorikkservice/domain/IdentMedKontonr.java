package no.nav.testnav.apps.syntvedtakshistorikkservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentMedKontonr {
    private String ident;
    private String kontonummer;
}
