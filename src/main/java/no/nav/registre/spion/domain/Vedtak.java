package no.nav.registre.spion.domain;

import java.time.LocalDate;
import java.util.Random;

import org.apache.commons.math3.distribution.GammaDistribution;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Vedtak {

    private final String identitetsnummer;
    private final String virksomhetsnummer;
    private final LocalDate fom;
    private final LocalDate tom;
    private final String ytelse = "SP";
    private final String vedtaksstatus;
    private final int sykemeldingsgrad;
    private final int refusjonsbelop;

    public Vedtak(LocalDate startDato, boolean isFoersteVedtak){

        LocalDate startDatoPeriode = isFoersteVedtak ? startDato: getNextStartDato(startDato);
        int periodeLength = getPeriodeLength();
        LocalDate sluttDatoPeriode = startDatoPeriode.plusDays(periodeLength);

        this.identitetsnummer = getID();
        this.virksomhetsnummer = getVnr();
        this.fom = startDatoPeriode;
        this.tom = sluttDatoPeriode;
        this.vedtaksstatus = getVedtaksstatus();
        this.sykemeldingsgrad = getSykemeldingsgrad();
        this.refusjonsbelop = getRefusjonsBeloepForPeriode(periodeLength);

    }

    /**
     * 25% av alle nye perioder må ligge kant-i-kant med en eksisterende periode for å simulere forlengelser.
     * For de resterende trekkes antallet dager mellom perioder fra en uniform fordeling mellom 1 og 21.
     * @param lastSluttDato
     * @return nextStartDato
     */
    private LocalDate getNextStartDato(LocalDate lastSluttDato){
        int numDays = Math.random() < 0.25 ? 0: new Random().nextInt(21) + 1;

        return lastSluttDato.plusDays(numDays);
    }


    /**
     *Lengden på en periode trekkes fra en gamma-fordeling med shape 1.5 og scale 14.
     * @return periodeLength
     **/
    private int getPeriodeLength(){
        GammaDistribution dist = new GammaDistribution(1.5, 14);
        int result = (int)dist.sample() + 1;

        return result > 248 ? 248 : result;
    }

    /**
     * TODO: endre på metoden slik at id (Fnr/Dnr) er korrekt og koblet opp mot personer i Mini-Norge
     * @return id
     */
    private String getID(){
        return "10101010101";
    }


    /**
     * TODO: endre på metoden slik at virksomhetsnummeret er korrekt og koblet opp mot personer i Mini-Norge
     * @return vnr
     */
    private String getVnr(){
        return "020202020";
    }


    /**
     * Status trekkes fra en fordeling slik at vi i snitt får 90% innvilget og 10 % avslått.
     * @return vedtaksstatus
     */
    private String getVedtaksstatus(){
        return Math.random() <0.1 ? "Avslått": "Innvilget";
    }


    /**
     * Sykmeldingsgrad kan trekkes fra en uniform fordeling mellom 20 og 100. Eventuelt si at 50% av alle sykmeldingsgrader
     * er på 100%, og så fordele uniformt mellom 20% og 90%.
     * @return sykemeldingsgrad
     */
    private int getSykemeldingsgrad(){
        return Math.random()<0.5 ? 100 : 20 + new Random().nextInt(71);
    }


    /**
     * Refusjonsbeløpet kan trekkes fra en uniform fordeling mellom 200 og 2400 og så gange opp
     * med antallet dager i perioden (dette blir ikke helt riktig da vi også kan ta med ikke-virkedager).
     * @param periodeLength
     * @return refusjonsbeløp for perioden
     */
    private int getRefusjonsBeloepForPeriode(int periodeLength){
        return (200 + new Random().nextInt(2201))*periodeLength;
    }

}
