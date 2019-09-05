package no.nav.registre.tss.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

@Slf4j
@Getter
@NoArgsConstructor
public class Person {

    private String fnr;
    private String navn;
    private Integer alder;

    public Person(String fnr, String navn) {
        this.fnr = fnr;
        this.navn = navn;
        this.alder = setalder();
    }

    private Integer setalder() {
        String decade;
        int control = Integer.parseInt(fnr.substring(6, 7));

        if (control > 5) {
            decade = "20";
        } else {
            decade = "19";
        }

        String dateString = fnr.substring(0, 4) + decade + fnr.substring(4, 6);
        try {
            return Period.between(
                    new SimpleDateFormat("ddMMyyyy").parse(dateString).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    LocalDate.now())
                    .getYears();
        } catch (ParseException e) {
            log.error("Kunne ikke opprette dato", e);
            return null;
        }
    }
}