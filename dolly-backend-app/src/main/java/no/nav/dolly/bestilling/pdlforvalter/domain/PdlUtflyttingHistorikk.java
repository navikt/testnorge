package no.nav.dolly.bestilling.pdlforvalter.domain;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtflyttingHistorikk {

    private List<PdlUtflytting> utflyttinger;

    public List<PdlUtflytting> getUtflyttinger() {
        if (isNull(utflyttinger)) {
            utflyttinger = new ArrayList<>();
        }
        return utflyttinger;
    }
}