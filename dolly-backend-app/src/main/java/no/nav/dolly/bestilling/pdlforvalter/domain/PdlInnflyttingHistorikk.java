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
public class PdlInnflyttingHistorikk {

    private List<PdlInnflytting> innflyttinger;

    public List<PdlInnflytting> getInnflyttinger() {
        if (isNull(innflyttinger)) {
            innflyttinger = new ArrayList<>();
        }
        return innflyttinger;
    }
}