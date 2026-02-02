package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skattekort {

    private LocalDate utstedtDato;
    private Long skattekortidentifikator;
    private List<Forskuddstrekk> forskuddstrekk;

    public List<Forskuddstrekk> getForskuddstrekk() {

        if (isNull(forskuddstrekk)) {
            forskuddstrekk = new ArrayList<>();
        }

        return forskuddstrekk;
    }
}
