package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

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