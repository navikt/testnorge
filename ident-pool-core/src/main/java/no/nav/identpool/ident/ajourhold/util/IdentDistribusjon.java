package no.nav.identpool.ident.ajourhold.util;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
class IdentDistribusjon {

    public Integer antallPersonerForDag(LocalDate localDate) {
        double antallForAar = verdiFraDistribusjon(localDate.now().getYear() - localDate.getYear());
        return (int) (Math.ceil(antallForAar/365));
    }

    public Integer hentKritiskAntallForTidsintervall(Integer fraAar, Integer tomAar) {
        return (int) (verdiFraDistribusjon(fraAar) + verdiFraDistribusjon(tomAar+1))*(tomAar+1-fraAar)/2;
    }

    private Double verdiFraDistribusjon(Integer aar) {
        double aarDouble = (double) aar;
        return (Math.floor(60000.0/20.0*(Math.exp(-aarDouble/40.0) * Math.sqrt(aarDouble))/(2.0 * Math.sqrt(10.0 * 3.14159265359))));
    }
}
