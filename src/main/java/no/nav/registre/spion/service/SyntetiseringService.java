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
import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;

@Service
@Slf4j
public class SyntetiseringService {

    private final String YTELSE = "SP";

    public List<Vedtak> syntetiserVedtak(SyntetiserSpionRequest request) {
        String endDate = request.getEndDate();
        String startDate = request.getStartDate();
        Integer numPeriods = request.getNumPeriods();

        LocalDate sluttDato = !endDate.equals("") ? LocalDate.parse(endDate) :
                startDate.equals("") ? LocalDate.now() : LocalDate.parse(startDate).plusMonths(18);

        LocalDate startDato = !startDate.equals("") ? LocalDate.parse(startDate) : sluttDato.minusMonths(18);

        int antallPerioder = numPeriods != null ? numPeriods : new Random().nextInt(15) + 1 ;

        return lagListeMedVedtak(startDato, sluttDato, antallPerioder);
    }

    private List<Vedtak> lagListeMedVedtak(
            LocalDate startDato,
            LocalDate sluttDato,
            int antallPerioder
    ){
        List<Vedtak> vedtaksliste = new ArrayList<>();

        int dagerFoerFoerstePeriode = new Random().nextInt(91);
        LocalDate startDatoFoerstePeriode = startDato.plusDays(dagerFoerFoerstePeriode);

        LocalDate lastSluttDato = startDatoFoerstePeriode;

        for(int i=0; i<antallPerioder; i++){

            LocalDate startDatoPeriode = i==0 ? startDatoFoerstePeriode: getNextStartDato(lastSluttDato);
            int periodeLength = getPeriodeLength();
            LocalDate sluttDatoPeriode = startDatoPeriode.plusDays(periodeLength);

            if(startDatoPeriode.isAfter(sluttDato) || startDatoPeriode.isAfter(LocalDate.now())){
                log.warn("Antall perioder ble for mange for valgt tidsrom.");
                break;
            }

            vedtaksliste.add(new Vedtak(
                    getID(),
                    getVnr(),
                    startDatoPeriode.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    sluttDatoPeriode.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    YTELSE,
                    getVedtaksstatus(),
                    getSykemeldingsgrad(),
                    getRefusjonsBeloepForPeriode(periodeLength)));

            lastSluttDato = sluttDatoPeriode;
        }
        log.info("Vedtak for {} periode(r) ble lagd.", vedtaksliste.size());
        return vedtaksliste;
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
