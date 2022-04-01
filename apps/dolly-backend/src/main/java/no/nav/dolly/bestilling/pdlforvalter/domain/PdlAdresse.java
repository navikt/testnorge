package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PdlAdresse extends PdlOpplysning {

    private String adresseIdentifikatorFraMatrikkelen;
    private Adressegradering adressegradering;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;
    private String coAdressenavn;
    private String naerAdresseIdentifikatorFraMatrikkelen;
    private PdlVegadresse vegadresse;

    public enum Adressegradering {UGRADERT, KLIENTADRESSE, FORTROLIG, STRENGT_FORTROLIG, STRENGT_FORTROLIG_UTLAND}

    public enum OppholdAnnetSted {MILITAER, UTENRIKS, PAA_SVALBARD, PENDLER}
}
