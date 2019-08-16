package no.nav.registre.inntekt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.inntektstub.domain.rs.RsInntekt;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class IdentMedData {
    private String id;
    private List<RsInntekt> data;
}
