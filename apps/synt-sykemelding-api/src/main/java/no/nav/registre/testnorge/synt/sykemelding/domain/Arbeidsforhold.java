package no.nav.registre.testnorge.synt.sykemelding.domain;

import lombok.Getter;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

@Getter
public class Arbeidsforhold {
    private final String navn;
    private final Float stillingsprosent;
    private final String yrkesbetegnelse;
    private final String gatenavn;
    private final String postnummer;
    private final String orgnr;
    private final String by;
    private final String land;

    public Arbeidsforhold(ArbeidsforholdDTO arbeidsforholdDTO, OrganisasjonDTO organisasjonDTO) {
        navn = organisasjonDTO.getNavn();
        stillingsprosent = arbeidsforholdDTO.getStillingsprosent();
        yrkesbetegnelse = arbeidsforholdDTO.getYrke();
        orgnr = organisasjonDTO.getOrgnummer();
        var adresse = organisasjonDTO.getForretningsadresser();
        gatenavn = adresse != null ? adresse.getAdresselinje1() : null;
        postnummer = adresse != null ? adresse.getPostnummer() : null;
        by = adresse != null ? adresse.getPoststed() : null;
        land = adresse != null ? adresse.getLandkode() : null;
    }
}
