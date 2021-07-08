package no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ArbeidsforholdDTO {

    AnsettelsesperiodeDTO ansettelsesperiode;
    List<AntallTimerForTimeloennetDTO> antallTimerForTimeloennet;
    List<ArbeidsavtaleDTO> arbeidsavtaler;
    String arbeidsforholdId;
    ArbeidsgiverDTO arbeidsgiver;
    ArbeidstakerDTO arbeidstaker;
    boolean innrapportertEtterAOrdningen;
    Long navArbeidsforholdId;
    ArbeidsgiverDTO opplysningspliktig;
    FartoyDTO fartoy;
    List<PermisjonPermitteringDTO> permisjonPermitteringer;
    LocalDateTime registrert;
    LocalDateTime sistBekreftet;
    String type;
    List<UtenlandsoppholdDTO> utenlandsopphold;
}


