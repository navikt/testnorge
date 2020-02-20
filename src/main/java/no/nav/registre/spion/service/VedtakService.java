package no.nav.registre.spion.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        int periodeLength = getPeriodeLength();

        Hashtable<String, String>  periode = getPeriode(info.getStartDate(), periodeLength);
        int refusjonsBelop = getRefusjonsBelop(periodeLength);

        return new Vedtak(
                "SP",
                periode,
                info.getPatient().getFnr(),
                "020202020",
                getVedtaksstatus(),
                Math.random() < 0.25 ? "Ja" : "Nei",
                getSykemeldingsgrad(),
                refusjonsBelop,
                refusjonsBelop/periodeLength,
                getMaksDato(periodeLength),
                new ArrayList<Hashtable<String, String>>());
    }

    private int getPeriodeLength(){
        // Lengden på en periode kan trekkes fra en pareto-fordeling med minimumsverdi på 1 dag og forventningsverdi(?) på 14 dager.
        return -(int)(Math.log(new Random().nextDouble()) / (1.0 / 14.0)) + 1;  //exponential-fordeling - burde bytte til pareto når parameterene er bestemt
}


    private Hashtable<String, String>  getPeriode(String startDate, int periodeLength){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try{
            c.setTime(sdf.parse(startDate));
        }catch(ParseException e){
            e.printStackTrace();
        }

        c.add(Calendar.DAY_OF_MONTH, periodeLength);
        String endDate = sdf.format(c.getTime());

        Hashtable<String, String> periode = new Hashtable<String, String>();
        periode.put("FOMDato", startDate);
        periode.put("TOMDato", endDate);
        return periode;
    }

    private String getVedtaksstatus(){
        //Status kan trekkes fra en fordeling slik at vi i snitt får 90% innvilget; 9.9% avslått; 0.1% henlagt.
        double r = Math.random();
        return r<0.01 ? "Henlagt": r<0.1 ? "Avslått" : "Innvilget";
    }


    private int getSykemeldingsgrad(){
        // Sykmeldingsgrad kan trekkes fra en uniform fordeling mellom 20 og 100. Eventuelt si at 50% av alle sykmeldingsgrader
        // er på 100%, og så fordele uniformt mellom 20% og 90%.
        return Math.random()<0.5 ? 100 : 20 + new Random().nextInt(31);
    }

    private int getRefusjonsBelop(int periodeLength){
        // Refusjonsbeløpet kan trekkes fra en uniform fordeling mellom 200 og 2400 og så gange opp
        // med antallet dager i perioden (dette blir ikke helt riktig da vi også kan ta med ikke-virkedager).
        return (200 + new Random().nextInt(2201))*periodeLength;
    }

    private int getMaksDato(int periodeLength){

        ParetoDistribution dist = new ParetoDistribution();

        int num_days = 248;
        num_days -= Math.random()<0.5 ? periodeLength : (int)(periodeLength*dist.sample());

        return num_days<0 ? 0 : num_days;
    }

}
