package no.nav.registre.spion.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.domain.SykemeldingInformation;
import no.nav.registre.spion.domain.Vedtak;
import org.apache.commons.math3.distribution.ParetoDistribution;


@Service
@Slf4j
public class VedtakService {

    public Vedtak generateVedtak(SykemeldingInformation info){

        int periodeLength = getPeriodeLength();

        Hashtable<String, String>  periode = getPeriode(info.getStartDate(), periodeLength);
        int refusjonsBelop = getRefusjonsBelop(periodeLength);

        return new Vedtak(
                "SP",
                periode,
                info.getPatient().getFnr(),
                getVnr(),
                getVedtaksstatus(),
                getFritakFraAGPStatus(),
                getSykemeldingsgrad(),
                refusjonsBelop,
                refusjonsBelop/periodeLength,
                getMaksDato(periodeLength),
                getFerie());
    }

    /**
     *Lengden på en periode kan trekkes fra en pareto-fordeling med minimumsverdi på 1 dag og forventningsverdi på 14 dager.
     * TODO: bør bytte til pareto når parameterene er bestemt, metoden bruker nå eksponentiell-fordeling
     * @return
     **/
    private int getPeriodeLength(){
        return -(int)(Math.log(new Random().nextDouble()) / (1.0 / 14.0)) + 1;
    }

    /**
     * Tar inn startdato for perioden og legger til periodelengden for å finne sluttdatoen for perioden.
     * @param startDate
     * @param periodeLength
     * @return perioden med start og sluttdato
     */
    private Hashtable<String, String>  getPeriode(String startDate, int periodeLength){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try{
            c.setTime(sdf.parse(startDate));
        }catch(ParseException e){
            log.error("Error occurred while parsing startDate", e);
        }

        c.add(Calendar.DAY_OF_MONTH, periodeLength);
        String endDate = sdf.format(c.getTime());

        Hashtable<String, String> periode = new Hashtable<String, String>();
        periode.put("FOMDato", startDate);
        periode.put("TOMDato", endDate);
        return periode;
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
     * Status for fritak fra AGP trekkes fra en fordeling der 25% er Ja og 75% er Nei.
     * TODO: endre på metoden når vi har fått info om korrekt fordeling.
     * @return
     */
    private String getFritakFraAGPStatus(){
       return Math.random() < 0.25 ? "Ja" : "Nei";
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

    /**
     * MaksDato blir beregnet til 248-periodelengden i 50% av tilfellene. I de gjenværende tilfellene blir den beregnet til
     * 248 - periodelengen*sample_fra_pareto_fordeling.
     * TODO: endre på metoden når vi har får info om hvordan maksdato skal egentlig beregnes.
     * @param periodeLength
     * @return maksDato
     */
    private int getMaksDato(int periodeLength){

        ParetoDistribution dist = new ParetoDistribution();

        int num_days = 248;
        num_days -= Math.random()<0.5 ? periodeLength : (int)(periodeLength*dist.sample());

        return num_days<0 ? 0 : num_days;
    }

    /**
     * Returnerer en tom liste for ferie.
     * TODO: endre på metoden når vi har fått info om hvordan ferie bør beregnes.
     * @return liste over ferier
     */
    private List<Hashtable<String, String>> getFerie(){
        return new ArrayList<Hashtable<String, String>>();
    }
}
