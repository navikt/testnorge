package no.nav.kodeverk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Kodeverk {

    private String navn;
    private String type;
    private int versjon;
    private LocalDate gyldigFra;
    private LocalDate gyldigTil;
    private List<Kode> koder;

}
