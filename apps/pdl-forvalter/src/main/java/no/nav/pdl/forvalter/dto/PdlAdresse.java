package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PdlAdresse {

    private String adresseIdentifikatorFraMatrikkelen;
    private Adressegradering adressegradering;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;
    private String coAdressenavn;
    private String kilde;
    private Master master;
    private String naerAdresseIdentifikatorFraMatrikkelen;
    private PdlVegadresse vegadresse;
    public enum Adressegradering {UGRADERT, KLIENTADRESSE, FORTROLIG, STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND}
    public enum Bruksenhetstype {BOLIG, ANNET_ENN_BOLIG, FRITIDSBOLIG, IKKE_GODKJENT_BOLIG, UNUMMERERT_BRUKSENHET}
    public enum Master {FREG, PDL}
    public enum OppholdAnnetSted {MILITAER, UTENRIKS, PAA_SVALBARD, PENDLER}
}
