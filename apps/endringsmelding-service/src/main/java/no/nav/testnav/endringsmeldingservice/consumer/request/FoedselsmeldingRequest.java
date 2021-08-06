package no.nav.testnav.endringsmeldingservice.consumer.request;

import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

import no.nav.testnav.libs.dto.endringsmelding.v1.AdresseFra;
import no.nav.testnav.libs.dto.endringsmelding.v1.FoedselsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v1.Kjoenn;

@Value
public class FoedselsmeldingRequest {

    String identMor;
    String identFar;
    String identtype;
    LocalDate foedselsdato;
    String kjonn;
    String adresseFra;
    Set<String> miljoer;

    public FoedselsmeldingRequest(FoedselsmeldingDTO dto, Set<String> miljoer) {
        identMor = dto.getIdentMor();
        identFar = dto.getIdentFar();
        identtype = dto.getIdenttype();
        foedselsdato = dto.getFoedselsdato();
        kjonn = convert(dto.getKjoenn());
        adresseFra = convert(dto.getAdresseFra());
        this.miljoer = miljoer;
    }

    private static String convert(Kjoenn kjoenn) {
        if (kjoenn == null) {
            return "U";
        }
        switch (kjoenn) {
            case GUTT:
                return "M";
            case JENTE:
                return "K";
            case UKJENT:
            default:
                return "U";
        }
    }

    private static String convert(AdresseFra adresseFra) {
        if (adresseFra == null) {
            return "LAGNY";
        }
        switch (adresseFra) {
            case ARV_FRA_MORS:
                return "MOR";
            case ARV_FRA_FARS:
                return "FAR";
            case LAG_NY_ADRESSE:
            default:
                return "LAGNY";
        }
    }
}
