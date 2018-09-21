package no.nav.identpool.ident.ajourhold.util;

import org.springframework.stereotype.Service;

@Service
class IdentDistribusjon {

    Integer antallPersonerPerDag(Integer fraAar, Integer tomAar) {
        Integer antallDager = 365*(tomAar-fraAar+1);
        return (int) Math.ceil((double) hentKritiskAntallForTidsintervall(fraAar, tomAar)/(double) antallDager);
    }

    private Integer hentKritiskAntallForTidsintervall(Integer fraAar, Integer tomAar) {
        return (verdiFraDistribusjon(fraAar) + verdiFraDistribusjon(tomAar+1))*(tomAar+1-fraAar)/2;
    }

    private Integer verdiFraDistribusjon(Integer aar) {
        double aarDouble = (double) aar;
        return (int) (Math.floor(60000.0/20.0*(Math.exp(-aarDouble/40.0) * Math.sqrt(aarDouble))/(2.0 * Math.sqrt(10.0 * 3.14159265359))));
    }
}
