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
public class PdlUtflyttingHistorikk {

    private List<PdlUtflytting> utflyttinger;

    public List<PdlUtflytting> getUtflyttinger() {
        if (isNull(utflyttinger)) {
            utflyttinger = new ArrayList<>();
        }
        return utflyttinger;
    }
}