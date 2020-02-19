package no.nav.registre.spion.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
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

        int periodeDays = getPeriodeDays();
        log.info("Periodedager: " + periodeDays);

        Hashtable<String, String>  periode = getPeriode(info.getStartDate(), periodeDays);
        int refusjonsBelop = getRefusjonsBelop(periodeDays);

        return new Vedtak(
                "SP",
                periode,
                info.getPatient().getFnr(),
                getVnr(),
                getVedtaksstatus(info),
                getFritakFraAGP(),
                getSykemeldingsgrad(),
                refusjonsBelop,
                refusjonsBelop/periodeDays,
                "",
                "");
    }

    private int getPeriodeDays(){
//        Lengden på en periode kan trekkes fra en pareto-fordeling med minimumsverdi på 1 dag og forventningsverdi på 14 dager.

        ParetoDistribution par = new ParetoDistribution();

        return (int)par.sample();
    }

    private Hashtable<String, String>  getPeriode(String startDate, int periodeDays){
//        Tiden mellom to etterfølgende perioder kan trekkes fra en uniform fordeling på 0-60 dager. Det bør også være en
//        initielt tidsrom før første periode for å hindre at alle perioder starter samtidig. Lengden på en periode kan
//        trekkes fra en pareto-fordeling med minimumsverdi på 1 dag og forventningsverdi på 14 dager.

        //Specifying date format that matches the given date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try{
            //Setting the date to the given date
            c.setTime(sdf.parse(startDate));
        }catch(ParseException e){
            e.printStackTrace();
        }

        //Adding periode days
        c.add(Calendar.DAY_OF_MONTH, periodeDays);
        //Date after adding the days to the given date
        String endDate = sdf.format(c.getTime());

        Hashtable<String, String> periode = new Hashtable<String, String>();
        periode.put("fraDato", startDate);
        periode.put("tilDato", endDate);
        return periode;
    }

    private String getVnr(){
        return "020202020";
    }

    private String getVedtaksstatus(SykemeldingInformation info){
//        Status kan trekkes fra en fordeling slik at vi i snitt får 90% innvilget; 9.9% avslått; 0.1% henlagt.
        double r = Math.random();
        if (r<0.01){
            return "Henlagt";
        }else if(r<0.1){
            return "Avslått";
        }else{
            return "Innvilget";
        }
    }

    private String getFritakFraAGP(){
        double r = Math.random();

        if(r<0.5){
            return "Ja";
        }else{
            return "Nei";
        }
    }

    private int getSykemeldingsgrad(){
        double r = Math.random();
        if(r>=0.5){
            return 100;
        }else{
            return 20 + new Random().nextInt(31);
        }
    }

    private int getRefusjonsBelop(int periodedager){
//        Refusjonsbeløpet kan trekkes fra en uniform fordeling mellom 200 og 2400 og så gange opp
//        med antallet dager i perioden (dette blir ikke helt riktig da vi også kan ta med ikke-virkedager).
        double r = Math.random();
        return (200 + new Random().nextInt(2201))*periodedager;
    }

}
