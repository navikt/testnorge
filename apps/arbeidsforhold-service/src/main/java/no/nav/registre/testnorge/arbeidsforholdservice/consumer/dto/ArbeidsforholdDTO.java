package no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto;

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
    String arbeidsforholdId;
    ArbeidsgiverDTO arbeidsgiver;
    List<ArbeidsavtaleDTO> arbeidsavtaler;
    ArbeidstakerDTO arbeidstaker;
    String type;
    List<PermisjonPermitteringDTO> permisjonPermitteringer;
    LocalDateTime registrert;
    List<UtenlandsoppholdDTO> utenlandsopphold;
}
