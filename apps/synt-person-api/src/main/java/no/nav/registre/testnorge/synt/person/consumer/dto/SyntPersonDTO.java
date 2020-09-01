package no.nav.registre.testnorge.synt.person.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyntPersonDTO {
    String slektsnavn;
    String fornavn;
    String adressenavn;
    String postnummer;
    String kommunenummer;
    String fodselsdato;

    @JsonIgnore
    public LocalDate getConvertedFodselsdato() {
        if (fodselsdato == null) {
            return null;
        }

        var chars = fodselsdato.chars().mapToObj(c -> (char) c).map(String::valueOf).collect(Collectors.toList());

        String shortYear = chars.get(4) + chars.get(5);
        String month = chars.get(2) + chars.get(3);
        String day = chars.get(0) + chars.get(1);
        int numberOfHundredYears =  getNumberOfHundredYears();

        var thisCentury = LocalDate.parse(numberOfHundredYears + shortYear + "-" + month + "-" + day);
        var lastCentury = LocalDate.parse((numberOfHundredYears - 1) + shortYear + "-" + month + "-" + day);

        if (thisCentury.isAfter(LocalDate.now())) {
            return lastCentury;
        }
        return thisCentury;
    }

    private static int getNumberOfHundredYears() {
        return (int) Math.floor(LocalDate.now().getYear() / 100.0);
    }
}