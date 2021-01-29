package no.nav.registre.sdforvalter.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

import no.nav.registre.sdforvalter.domain.Adresse;
import no.nav.registre.sdforvalter.domain.Ereg;

@Data
@AllArgsConstructor
@Builder
public class OrganisasjonNaermereAvro {

    private final String orgnummer;
    private final String enhetstype;
    private final String navn;
    //private final String redigertNavn;
    private final String internettadresse;
    private final String epost;
    private final String sektorkode;
    private final String stiftelsesdato; //sjekk hvilken type dato
    private final String mobiltelefon;
    private final String naeringskode;
    private final String maalform;
    private final String telefon;
    private final String formaal;
    private List<OrganisasjonNaermereAvro> underenheter;
    private final Adresse forretningsAdresse;
    private final Adresse postadresse;

    public OrganisasjonNaermereAvro(Ereg org) {
        this.orgnummer = org.getOrgnr();
        this.enhetstype = org.getEnhetstype();
        this.navn = org.getNavn();
        this.internettadresse = org.getInternetAdresse();
        this.epost = org.getEpost();
        this.sektorkode = null;
        this.stiftelsesdato = null;
        this.mobiltelefon = null;
        this.naeringskode = org.getNaeringskode();
        this.maalform = null;
        this.telefon = null;
        this.formaal = null;
        this.underenheter = Collections.emptyList();
        this.forretningsAdresse = org.getForretningsAdresse();
        this.postadresse = org.getPostadresse();
    }

    public void addUnderenhet (OrganisasjonNaermereAvro underenhet) {
        this.underenheter.add(underenhet);
    }
}
