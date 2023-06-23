package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArenaResponse {

    String fodselsnr;
    LocalDate registrertDato;
    LocalDate sistInaktivDato;
    String maalform;
    String statsborgerLand;
    String bosattStatus;
    NavKontor lokalkontor;
    String hovedmaal;
    Egenskap formidlingsgruppe;
    Egenskap servicegruppe;
    Egenskap rettighetsgruppe;
    Boolean meldeplikt;
    String meldeform;
    String meldegruppe;
    List<Vedtak> vedtakListe;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class NavKontor {

        String enhetNr;
        String enhetNavn;
    }

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Egenskap {

        String kode;
        String navn;
    }

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Sak extends Egenskap {

        String status;
        Integer sakNr;
    }


    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Vedtak {

        Sak sak;
        Integer vedtakNr;
        Egenskap rettighet;
        Egenskap aktivitetfase;
        Egenskap type;
        Egenskap status;
        String utfall;
        LocalDate fraDato;
        LocalDate tilDato;
    }
}
