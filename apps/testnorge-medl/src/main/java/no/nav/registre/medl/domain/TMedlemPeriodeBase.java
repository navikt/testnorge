package no.nav.registre.medl.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TMedlemPeriodeBase {

    private LocalDate periodeFom;
    private LocalDate periodeTom;
    private String type;
    private String status;
    private String soknad;
    private String dekning;
    private String lovvalg;
    private String statusaarsak;
    private LocalDateTime datoBrukFra;
    private LocalDateTime datoBrukTil;
    private String grunnlag;
    private String land;
}
