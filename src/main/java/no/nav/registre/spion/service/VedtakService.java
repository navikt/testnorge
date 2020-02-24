package no.nav.registre.spion.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.domain.Vedtak;


@Service
@Slf4j
public class VedtakService {

    private final String YTELSE = "SP";

    public List<Vedtak> generateVedtak(int antallNyeVedtak){
        List<Vedtak> vedtaksliste = new ArrayList<>();

        LocalDate lastSluttDato = LocalDate.now().minusMonths(18);

        for(int i=0; i<antallNyeVedtak; i++){

            LocalDate startDato = getNextStartDato(lastSluttDato);

            int periodeLength = getPeriodeLength();
            int refusjonsBelop = getRefusjonsBelop(periodeLength);

            LocalDate sluttDato = startDato.plusDays(periodeLength);

            vedtaksliste.add(new Vedtak(
                    getID(),
                    getVnr(),
                    startDato.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    sluttDato.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    YTELSE,
                    getVedtaksstatus(),
                    getSykemeldingsgrad(),
                    refusjonsBelop));

            lastSluttDato = sluttDato;
        }

        return vedtaksliste;
    }


    /**
     * Tar inn sluttdato for forrige periode og legger til antall dager for å finne startdato for neste periode.
     * 20% av tiden er antall dager lik 0, 50% av tiden er det antall dager trekket fra en uniform fordeling mellom 1 og 20,
     * 30% av tiden trekkes antall dager fro en uniform fordeling mellom 21 og 60.
     * @param lastSluttDato
     * @return nextStartDato
     */
    private LocalDate getNextStartDato(LocalDate lastSluttDato){
        double r = Math.random();
        Random rand = new Random();

        int numDays = r < 0.3 ? 0: r<0.7 ? 1 + rand.nextInt(20) : 21 + rand.nextInt(40);

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
     * Status kan trekkes fra en fordeling slik at vi i snitt får 90% innvilget; 9.9% avslått; 0.1% henlagt.
     * @return vedtaksstatus
     */
    private String getVedtaksstatus(){
        double r = Math.random();
        return r<0.01 ? "Henlagt": r<0.1 ? "Avslått" : "Innvilget";
    }


    /**
     * Sykmeldingsgrad kan trekkes fra en uniform fordeling mellom 20 og 100. Eventuelt si at 50% av alle sykmeldingsgrader
     * er på 100%, og så fordele uniformt mellom 20% og 90%.
     * @return sykemeldingsgrad
     */
    private int getSykemeldingsgrad(){
        return Math.random()<0.5 ? 100 : 20 + new Random().nextInt(31);
    }


    /**
     * Refusjonsbeløpet kan trekkes fra en uniform fordeling mellom 200 og 2400 og så gange opp
     * med antallet dager i perioden (dette blir ikke helt riktig da vi også kan ta med ikke-virkedager).
     * @param periodeLength
     * @return refusjonsbeløp for perioden
     */
    private int getRefusjonsBelop(int periodeLength){
        return (200 + new Random().nextInt(2201))*periodeLength;
    }

}
