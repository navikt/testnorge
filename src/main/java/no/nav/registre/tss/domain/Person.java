package no.nav.registre.tss.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

@Getter
@NoArgsConstructor
public class Person {

    private String fnr;
    private String navn;
    private int alder;

    public Person(String fnr, String navn) {
        this.fnr = fnr;
        this.navn = navn;
        this.alder = setalder();
    }

    public int setalder() {
        String decade;
        LocalDate personBirth;
        int controll = Integer.parseInt(fnr.substring(6, 7));

        if (controll > 5) {
            decade = "20";
        } else {
            decade = "19";
        }

        String dateString = fnr.substring(0, 4) + decade + fnr.substring(4, 6);
        Date date = null;
        try {
            date = new SimpleDateFormat("ddMMyyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        personBirth = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(personBirth, LocalDate.now());
        return period.getYears();
    }
}