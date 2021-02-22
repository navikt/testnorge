package no.nav.registre.testnorge.arena.consumer.rs.response.tpsf;

import java.time.LocalDate;

import org.apache.tomcat.jni.Local;

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
public class BostedAdresse {

    private String adresseType;
    private OffentligAdresse offAdresse;
    private LocalDate tidspunktReg;
    private String kommunenr;
    private String landKode;
    private String saksbehandler;
    private LocalDate datoFom;
    private LocalDate datoTom;
    private String kommuneNavn;
    private String adresse1;
    private String adresse2;
    private String tilleggsAdresseSKD;
    private String poststed;
    private String bolignr;
    private String beskrAdrType;
    private MatrikkelAdresse matrAdresse;
    private String system;
    private Integer postnr;
    private String land;
}
