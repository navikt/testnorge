package no.nav.identpool.ajourhold.util;

import static java.time.LocalDate.now;

import org.springframework.stereotype.Service;

@Service
public class IdentDistribusjon {

    // empiriske konstanter for å få til en fin chi^2-fordeling
    private static final double C1 = 60_000;
    private static final double C2 = 20;
    private static final double C3 = 40;
    private static final double C4 = 2;
    private static final double C5 = 10;

    public Integer antallPersonerPerDagPerAar(int year) {
        double antallForAar = verdiFraDistribusjon(now().getYear() - year + 1);
        return (int) (Math.ceil(antallForAar / 365.0));
    }

    private Double verdiFraDistribusjon(Integer aar) {
        double aarDouble = (double) aar;
        return (Math.floor(C1 / C2 * (Math.exp(-aarDouble / C3) * Math.sqrt(aarDouble)) / (C4 * Math.sqrt(C5 * Math.PI))));

    }
}
