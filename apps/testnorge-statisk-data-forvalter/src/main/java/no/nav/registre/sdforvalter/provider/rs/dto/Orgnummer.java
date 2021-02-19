package no.nav.registre.sdforvalter.provider.rs.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Orgnummer {
    List<String> liste;
}
