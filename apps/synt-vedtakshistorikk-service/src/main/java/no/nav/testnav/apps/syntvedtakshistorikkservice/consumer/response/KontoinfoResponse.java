package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response;

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
public class KontoinfoResponse {

    private String fnr;
    private String kortnavn;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private String kontonummer;
    private String navn;
    private String adresseLinje1;
    private String adresseLinje2;
    private String adresseLinje3;
    private String postnr;
    private String landkode;
}