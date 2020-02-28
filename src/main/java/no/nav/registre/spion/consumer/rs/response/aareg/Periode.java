package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
public class Periode{
    private final LocalDate fom;
}